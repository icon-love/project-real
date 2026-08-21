package com.admin.config;

import com.admin.entity.GalleryCategory;
import com.admin.entity.GalleryImage;
import com.admin.entity.Member;
import com.admin.entity.Notice;
import com.admin.entity.Product;
import com.admin.entity.ProductReview;
import com.admin.entity.Store;
import com.admin.entity.SysAdmin;
import com.admin.entity.SysMenu;
import com.admin.entity.SysRole;
import com.admin.entity.SysRoleMenu;
import com.admin.entity.TradeOrder;
import com.admin.mapper.GalleryCategoryMapper;
import com.admin.mapper.GalleryImageMapper;
import com.admin.mapper.MemberMapper;
import com.admin.mapper.NoticeMapper;
import com.admin.mapper.ProductMapper;
import com.admin.mapper.ProductReviewMapper;
import com.admin.mapper.StoreMapper;
import com.admin.mapper.SysAdminMapper;
import com.admin.mapper.SysMenuMapper;
import com.admin.mapper.SysRoleMapper;
import com.admin.mapper.SysRoleMenuMapper;
import com.admin.mapper.TradeOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 种子数据初始化器：空库启动时自动写入演示数据（幂等）。
 * <p>在 {@code spring.sql.init} 建表完成后执行，若已存在数据（sys_admin 非空）则直接跳过。</p>
 * <ul>
 *   <li>菜单（8 条，与前端 mock 完全一致）</li>
 *   <li>角色（超级管理员 / 运营人员 / 访客）及角色-菜单关联</li>
 *   <li>管理员（admin / editor / guest，密码均为 123456，BCrypt 加密）</li>
 *   <li>图库分类 + 生成的 SVG 占位图（写入上传目录并入库）</li>
 *   <li>公告 / 商品 / 会员 / 店铺 / 交易订单（跨近 12 个月）/ 商品评论</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
    private static final String DEFAULT_PASSWORD = "123456";

    private final SysAdminMapper adminMapper;
    private final SysRoleMapper roleMapper;
    private final SysMenuMapper menuMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final GalleryCategoryMapper galleryCategoryMapper;
    private final GalleryImageMapper galleryImageMapper;
    private final NoticeMapper noticeMapper;
    private final ProductMapper productMapper;
    private final MemberMapper memberMapper;
    private final StoreMapper storeMapper;
    private final TradeOrderMapper tradeOrderMapper;
    private final ProductReviewMapper reviewMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.upload.dir:./uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (adminMapper.selectCount(null) > 0) {
            log.info("检测到系统已存在数据，跳过种子数据初始化");
            return;
        }
        log.info("开始初始化种子数据……");
        writeSeedFiles();
        seedMenus();
        seedRoles();
        seedAdmins();
        seedGallery();
        seedNotices();
        seedProducts();
        seedMembers();
        seedStores();
        seedOrders();
        seedReviews();
        log.info("种子数据初始化完成（账号 admin / editor / guest，密码均为 123456）");
    }

    // ================= 上传目录占位图片 =================

    /** 生成 SVG 占位图片与头像，写入上传目录（幂等：已存在则跳过） */
    private void writeSeedFiles() throws Exception {
        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(dir);

        String[] colors = {"#409EFF", "#67C23A", "#E6A23C", "#F56C6C",
                "#909399", "#9C27B0", "#00BCD4", "#FF9800"};
        for (int i = 1; i <= 8; i++) {
            writeIfAbsent(dir, "seed-" + i + ".svg", svg(colors[i - 1], "IMG " + i));
        }
        writeIfAbsent(dir, "avatar-admin.svg", svg("#409EFF", "管"));
        writeIfAbsent(dir, "avatar-editor.svg", svg("#67C23A", "李"));
        writeIfAbsent(dir, "avatar-guest.svg", svg("#909399", "王"));
    }

    private void writeIfAbsent(Path dir, String filename, String content) throws Exception {
        Path file = dir.resolve(filename);
        if (!Files.exists(file)) {
            Files.write(file, content.getBytes(StandardCharsets.UTF_8));
        }
    }

    private String svg(String color, String text) {
        return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"400\" height=\"300\">"
                + "<rect width=\"100%\" height=\"100%\" fill=\"" + color + "\"/>"
                + "<text x=\"50%\" y=\"50%\" font-size=\"64\" fill=\"#ffffff\" "
                + "text-anchor=\"middle\" dominant-baseline=\"middle\" font-family=\"sans-serif\">"
                + text + "</text></svg>";
    }

    // ================= 菜单 / 角色 / 管理员 =================

    /** 菜单（与前端 mock 的菜单树一致：id 1-8） */
    private void seedMenus() {
        List<SysMenu> menus = new ArrayList<>();
        menus.add(menu(1L, 0L, "仪表盘", "Dashboard", "dashboard", "dashboard/index", "Odometer", 1, "dashboard:view"));
        menus.add(menu(2L, 0L, "图库管理", "Gallery", "gallery", "gallery/index", "Picture", 2, "gallery:view"));
        menus.add(menu(3L, 0L, "公告管理", "Notice", "notice", "notice/index", "Bell", 3, "notice:view"));
        menus.add(menu(8L, 0L, "商品管理", "Product", "product", "product/index", "Goods", 4, "product:view"));
        menus.add(menu(4L, 0L, "系统管理", "System", "system", "", "Setting", 5, ""));
        menus.add(menu(5L, 4L, "管理员管理", "Admin", "admin", "admin/index", "User", 1, "system:admin"));
        menus.add(menu(6L, 4L, "菜单权限", "Menu", "menu", "menu/index", "Menu", 2, "system:menu"));
        menus.add(menu(7L, 4L, "角色管理", "Role", "role", "role/index", "Avatar", 3, "system:role"));
        for (SysMenu m : menus) {
            menuMapper.insert(m);
        }
    }

    private SysMenu menu(Long id, Long parentId, String title, String name, String path,
                         String component, String icon, Integer sort, String perms) {
        SysMenu m = new SysMenu();
        m.setId(id);
        m.setParentId(parentId);
        m.setTitle(title);
        m.setName(name);
        m.setPath(path);
        m.setComponent(component);
        m.setIcon(icon);
        m.setSort(sort);
        m.setVisible(true);
        m.setStatus(1);
        m.setPerms(perms);
        m.setCreateTime(new Date());
        return m;
    }

    /** 角色 + 角色-菜单关联 */
    private void seedRoles() {
        seedRole(1L, "超级管理员", "拥有系统全部权限", Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L));
        seedRole(2L, "运营人员", "负责内容维护（仪表盘/图库/公告/商品）", Arrays.asList(1L, 2L, 3L, 8L));
        seedRole(3L, "访客", "仅可查看仪表盘", Arrays.asList(1L));
    }

    private void seedRole(Long id, String name, String remark, List<Long> permissionIds) {
        SysRole role = new SysRole();
        role.setId(id);
        role.setName(name);
        role.setRemark(remark);
        role.setStatus(1);
        role.setCreateTime(new Date());
        roleMapper.insert(role);
        for (Long menuId : permissionIds) {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(id);
            rm.setMenuId(menuId);
            roleMenuMapper.insert(rm);
        }
    }

    /** 管理员（guest 默认禁用） */
    private void seedAdmins() {
        seedAdmin(1L, "admin", "超级管理员", 1L, "13800000001", "admin@example.com",
                "/uploads/avatar-admin.svg", 1);
        seedAdmin(2L, "editor", "运营小李", 2L, "13800000002", "editor@example.com",
                "/uploads/avatar-editor.svg", 1);
        seedAdmin(3L, "guest", "访客小王", 3L, "13800000003", "guest@example.com",
                "/uploads/avatar-guest.svg", 0);
    }

    private void seedAdmin(Long id, String username, String nickname, Long roleId,
                           String phone, String email, String avatar, Integer status) {
        SysAdmin admin = new SysAdmin();
        admin.setId(id);
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        admin.setNickname(nickname);
        admin.setRoleId(roleId);
        admin.setRoleName(roleName(roleId));
        admin.setPhone(phone);
        admin.setEmail(email);
        admin.setAvatar(avatar);
        admin.setStatus(status);
        admin.setCreateTime(new Date());
        adminMapper.insert(admin);
    }

    private String roleName(Long roleId) {
        return roleId != null && roleId == 1L ? "超级管理员"
                : roleId != null && roleId == 2L ? "运营人员" : "访客";
    }

    // ================= 图库 =================

    private void seedGallery() {
        seedCategory(1L, "首页轮播", 1, "首页轮播图");
        seedCategory(2L, "商品图片", 2, "");
        seedCategory(3L, "活动宣传", 3, "");
        seedCategory(4L, "其他", 4, "");

        String[] names = {"banner-home-01", "product-phone-01", "product-laptop-01", "activity-summer-01",
                "banner-spring-01", "product-shoes-01", "activity-618-01", "product-bag-01"};
        for (int i = 1; i <= 8; i++) {
            GalleryImage image = new GalleryImage();
            image.setCategoryId((i % 4) + 1L);
            image.setName(names[i - 1]);
            image.setUrl("/uploads/seed-" + i + ".svg");
            image.setSize(60 + i * 23);
            image.setCreateTime(new Date());
            galleryImageMapper.insert(image);
        }
    }

    private void seedCategory(Long id, String name, Integer sort, String remark) {
        GalleryCategory category = new GalleryCategory();
        category.setId(id);
        category.setName(name);
        category.setSort(sort);
        category.setRemark(remark);
        category.setCreateTime(new Date());
        galleryCategoryMapper.insert(category);
    }

    // ================= 公告 =================

    private void seedNotices() {
        String[][] data = {
                {"系统将于本周六凌晨进行升级维护",
                        "为保证系统稳定运行，计划于本周六 00:00-02:00 进行升级维护，期间部分功能可能不可用，请提前保存工作内容。"},
                {"「618 大促」活动物料提交提醒",
                        "请各运营同学于本周五前完成 618 大促活动页面的图片与文案物料提交，逾期将无法参与资源位排期。"},
                {"关于新增图片批量上传功能的公告",
                        "图库模块现已支持批量上传，单次最多 20 张，支持 jpg/png/webp 格式，单张不超过 10MB。"},
                {"新版后台登录安全策略上线",
                        "为保障账号安全，现已启用登录失败锁定策略：连续输错 5 次将锁定 30 分钟。"},
                {"数据看板指标口径调整说明",
                        "自本月起，「今日销售额」统计口径调整为含运费实付金额，与财务报表保持一致。"},
                {"角色权限配置功能上线公告",
                        "系统管理新增「角色管理」，可灵活为不同角色分配菜单权限，实现精细化权限控制。"}
        };
        for (int i = 0; i < data.length; i++) {
            Notice notice = new Notice();
            notice.setTitle(data[i][0]);
            notice.setContent(data[i][1]);
            notice.setAuthor(i % 2 == 0 ? "超级管理员" : "运营小李");
            notice.setStatus(1);
            notice.setCreateTime(dt(LocalDateTime.now(ZONE).minusDays(i * 3L)));
            noticeMapper.insert(notice);
        }
    }

    // ================= 商品 =================

    private void seedProducts() {
        Object[][] data = {
                {"智能手机 X1", "手机数码", 2999, 200, "/uploads/seed-1.svg",
                        "6.7 英寸高清全面屏，双卡双待，5000mAh 大电池长续航。"},
                {"轻薄笔记本 Pro", "手机数码", 5999, 80, "/uploads/seed-2.svg",
                        "14 英寸 2.8K 屏，16G 内存 + 512G SSD，轻薄便携，性能强劲。"},
                {"轻量运动跑鞋", "服饰鞋包", 399, 500, "/uploads/seed-3.svg",
                        "轻量缓震，透气网面，日常跑步通勤皆宜。"},
                {"商务双肩包", "服饰鞋包", 299, 300, "/uploads/seed-4.svg",
                        "大容量多隔层，防泼水面料，独立电脑仓保护。"},
                {"有机高山茶", "食品生鲜", 128, 1000, "/uploads/seed-5.svg",
                        "精选高山茶园原料，清香回甘，礼盒装更显心意。"},
                {"简约陶瓷水杯", "家居生活", 69, 800, "/uploads/seed-6.svg",
                        "温润釉面，大容量，可进微波炉与洗碗机。"}
        };
        for (int i = 0; i < data.length; i++) {
            Product product = new Product();
            product.setName((String) data[i][0]);
            product.setCategory((String) data[i][1]);
            product.setPrice(new BigDecimal((Integer) data[i][2]));
            product.setStock((Integer) data[i][3]);
            product.setImage((String) data[i][4]);
            product.setStatus(1);
            product.setDescription((String) data[i][5]);
            product.setCreateTime(dt(LocalDateTime.now(ZONE).minusDays((long) (data.length - i) * 2)));
            productMapper.insert(product);
        }
    }

    // ================= 会员 / 店铺 / 订单 / 评论 =================

    private void seedMembers() {
        String[][] data = {
                {"zhangsan", "张三", "13811110001"},
                {"lisi", "李四", "13811110002"},
                {"wangwu", "王五", "13811110003"},
                {"zhaoliu", "赵六", "13811110004"},
                {"sunqi", "孙七", "13811110005"},
                {"zhouba", "周八", "13811110006"}
        };
        for (int i = 0; i < data.length; i++) {
            Member member = new Member();
            member.setUsername(data[i][0]);
            member.setNickname(data[i][1]);
            member.setPhone(data[i][2]);
            member.setCreateTime(dt(LocalDateTime.now(ZONE).minusDays((long) (data.length - i) * 10)));
            memberMapper.insert(member);
        }
    }

    private void seedStores() {
        Object[][] data = {
                {"旗舰店", 45820, 3260},
                {"华东分店", 31560, 2140},
                {"华南分店", 27410, 1890},
                {"华北分店", 19870, 1230}
        };
        for (Object[] row : data) {
            Store store = new Store();
            store.setName((String) row[0]);
            store.setSales(new BigDecimal((Integer) row[1]));
            store.setTips(new BigDecimal((Integer) row[2]));
            storeMapper.insert(store);
        }
    }

    /** 交易订单：覆盖最近 12 个月（含今日），保证趋势图与今日统计有数据 */
    private void seedOrders() {
        Random random = new Random(20260821L);
        LocalDate today = LocalDate.now(ZONE);
        long seq = 0;

        // 今天固定几单，保证「今日销售额/订单数」非零
        for (int i = 0; i < 3; i++) {
            insertOrder(++seq, 1L + random.nextInt(4), 120 + random.nextInt(880),
                    random.nextInt(10) < 7 ? 3 : 1, today.atTime(9 + i * 3, random.nextInt(60)));
        }
        // 近 12 个月（不含今天所在月）各月若干单
        for (int m = 11; m >= 1; m--) {
            YearMonth ym = YearMonth.from(today).minusMonths(m);
            int count = 2 + random.nextInt(4);
            for (int i = 0; i < count; i++) {
                int day = 1 + random.nextInt(ym.lengthOfMonth());
                LocalDate date = ym.atDay(Math.min(day, ym.lengthOfMonth()));
                insertOrder(++seq, 1L + random.nextInt(4), 60 + random.nextInt(2940),
                        random.nextInt(10) < 8 ? 3 : (random.nextInt(2) == 0 ? 1 : 2),
                        date.atTime(8 + random.nextInt(14), random.nextInt(60)));
            }
        }
        // 本月到今天为止，再补几单
        int thisMonthDays = today.getDayOfMonth();
        for (int i = 0; i < 4; i++) {
            LocalDate date = today.minusDays(random.nextInt(thisMonthDays));
            insertOrder(++seq, 1L + random.nextInt(4), 80 + random.nextInt(1920),
                    random.nextInt(10) < 8 ? 3 : 2, date.atTime(9 + random.nextInt(13), random.nextInt(60)));
        }
    }

    private void insertOrder(long seq, long storeId, int amount, int status, LocalDateTime time) {
        TradeOrder order = new TradeOrder();
        order.setOrderNo("SO" + String.format("%012d", seq));
        order.setStoreId(storeId);
        order.setAmount(new BigDecimal(amount));
        order.setStatus(status);
        order.setCreateTime(dt(time));
        tradeOrderMapper.insert(order);
    }

    /** LocalDateTime(Asia/Shanghai) -> Date */
    private Date dt(LocalDateTime time) {
        return Date.from(time.atZone(ZONE).toInstant());
    }

    private void seedReviews() {
        String[] contents = {
                "包装很好，物流很快，商品质量不错！",
                "性价比很高，已经推荐给朋友了。",
                "整体满意，就是发货稍微慢了一点。",
                "客服态度很好，问题解决及时。",
                "第二次回购了，品质一如既往。"
        };
        for (int i = 0; i < contents.length; i++) {
            ProductReview review = new ProductReview();
            review.setProductId((long) (i % 6 + 1));
            review.setContent(contents[i]);
            review.setCreateTime(dt(LocalDateTime.now(ZONE).minusMinutes((long) i * 35)));
            reviewMapper.insert(review);
        }
    }
}
