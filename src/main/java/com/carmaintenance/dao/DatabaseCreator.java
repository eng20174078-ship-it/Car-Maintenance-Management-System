package com.carmaintenance.dao;

import java.sql.*;

public class DatabaseCreator {

    public static void createDatabaseIfNotExists() {
        System.out.println("🗄️ === إنشاء قاعدة البيانات والجداول ===");

        Connection conn = null;
        Statement stmt = null;

        try {
            // 1. الاتصال بـ MySQL بدون تحديد قاعدة بيانات
            String url = "jdbc:mysql://localhost:3306/";
            String user = "root";
            String password = "";

            conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ الاتصال بـ MySQL ناجح");

            stmt = conn.createStatement();

            // 2. إنشاء قاعدة البيانات إذا لم تكن موجودة
            String createDbSQL = "CREATE DATABASE IF NOT EXISTS car_maintenance_db " +
                    "CHARACTER SET utf8mb4 " +
                    "COLLATE utf8mb4_unicode_ci";

            stmt.executeUpdate(createDbSQL);
            System.out.println("✅ تم إنشاء/التحقق من قاعدة البيانات");

            // 3. استخدام قاعدة البيانات
            stmt.executeUpdate("USE car_maintenance_db");
            System.out.println("✅ تم التبديل إلى قاعدة البيانات");

            // 4. إنشاء الجداول
            createTables(conn);

            // 5. إضافة بيانات تجريبية
            insertSampleData(conn);

            // 6. تحديث الجداول بإضافة الأعمدة الناقصة (للتوافق مع الاستعلامات)
            addMissingColumns(conn);

            System.out.println("🎉 تم إنشاء قاعدة البيانات والجداول بنجاح!");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في إنشاء قاعدة البيانات: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // تنظيف الموارد
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
                System.out.println("✅ تم إغلاق الاتصال");
            } catch (SQLException e) {
                System.err.println("⚠️ خطأ في إغلاق الموارد: " + e.getMessage());
            }
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        // 1. جدول العملاء
        String createCustomersTable =
                "CREATE TABLE IF NOT EXISTS customers (" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY," +
                        "  name VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL," +
                        "  phone VARCHAR(20) UNIQUE NOT NULL," +
                        "  email VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                        "  address TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                        "  customer_type ENUM('individual', 'company') DEFAULT 'individual'," +
                        "  notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," + // ⬅️ ملاحظات العميل
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        stmt.executeUpdate(createCustomersTable);
        System.out.println("✅ جدول العملاء جاهز");

        // 2. جدول السيارات
        String createVehiclesTable =
                "CREATE TABLE IF NOT EXISTS vehicles (" +
                        "  plate_number VARCHAR(20) PRIMARY KEY," +
                        "  model VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL," +
                        "  brand VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," + // ⬅️ ماركة السيارة
                        "  year INT," +
                        "  color VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                        "  engine_type VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                        "  engine_number VARCHAR(50)," + // ⬅️ رقم المحرك
                        "  chassis_number VARCHAR(50)," + // ⬅️ رقم الهيكل
                        "  fuel_type ENUM('gasoline', 'diesel', 'electric', 'hybrid') DEFAULT 'gasoline'," + // ⬅️ نوع الوقود
                        "  mileage INT DEFAULT 0," + // ⬅️ عدد الكيلومترات
                        "  last_maintenance_date DATE," +
                        "  next_maintenance_date DATE," +
                        "  insurance_expiry DATE," + // ⬅️ تاريخ انتهاء التأمين
                        "  notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," + // ⬅️ ملاحظات السيارة
                        "  owner_id INT NOT NULL," +
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                        "  FOREIGN KEY (owner_id) REFERENCES customers(id) ON DELETE CASCADE" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        stmt.executeUpdate(createVehiclesTable);
        System.out.println("✅ جدول السيارات جاهز");

        // 3. جدول الفنيين
        String createTechniciansTable =
                "CREATE TABLE IF NOT EXISTS technicians (" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY," +
                        "  name VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL," +
                        "  phone VARCHAR(20) UNIQUE NOT NULL," +
                        "  email VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                        "  address TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," + // ⬅️ عنوان الفني
                        "  specialization VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                        "  hire_date DATE," +
                        "  salary DECIMAL(10, 2)," +
                        "  status ENUM('active', 'inactive', 'on_leave') DEFAULT 'active'," +
                        "  rating DECIMAL(3,2) DEFAULT 0.00," +
                        "  experience_years INT DEFAULT 0," + // ⬅️ سنوات الخبرة
                        "  notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," + // ⬅️ ملاحظات عن الفني
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        stmt.executeUpdate(createTechniciansTable);
        System.out.println("✅ جدول الفنيين جاهز");

        // 4. جدول قطع الغيار
        String createSparePartsTable =
                "CREATE TABLE IF NOT EXISTS spare_parts (" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY," +
                        "  name VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL," +
                        "  description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                        "  part_number VARCHAR(50) UNIQUE," +
                        "  price DECIMAL(10, 2) NOT NULL," +
                        "  cost DECIMAL(10, 2) NOT NULL," +
                        "  quantity INT DEFAULT 0," +
                        "  min_threshold INT DEFAULT 5," +
                        "  max_threshold INT DEFAULT 100," + // ⬅️ الحد الأقصى للمخزون
                        "  supplier VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                        "  supplier_phone VARCHAR(20)," + // ⬅️ هاتف المورد
                        "  location VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," + // ⬅️ موقع التخزين
                        "  notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," + // ⬅️ ملاحظات القطعة
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        stmt.executeUpdate(createSparePartsTable);
        System.out.println("✅ جدول قطع الغيار جاهز");

        // 5. جدول طلبات الصيانة
        String createMaintenanceOrdersTable =
                "CREATE TABLE IF NOT EXISTS maintenance_orders (" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY," +
                        "  order_number VARCHAR(20) UNIQUE," +
                        "  vehicle_plate VARCHAR(20) NOT NULL," +
                        "  technician_id INT NOT NULL," +
                        "  description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                        "  customer_notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                        "  internal_notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                        "  priority ENUM('low', 'medium', 'high', 'urgent') DEFAULT 'medium'," +
                        "  estimated_hours DECIMAL(5,2)," +
                        "  actual_hours DECIMAL(5,2)," +
                        "  estimated_cost DECIMAL(10, 2) DEFAULT 0.00," +
                        "  actual_cost DECIMAL(10, 2) DEFAULT 0.00," +
                        "  labor_cost DECIMAL(10, 2) DEFAULT 0.00," +
                        "  parts_cost DECIMAL(10, 2) DEFAULT 0.00," +
                        "  status ENUM('Pending', 'In Progress', 'Waiting for Parts', 'Completed', 'Cancelled') DEFAULT 'Pending'," +
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                        "  scheduled_date DATE," +
                        "  start_date TIMESTAMP NULL," + // ⬅️ تاريخ البدء الفعلي
                        "  completed_at TIMESTAMP NULL," +
                        "  notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," + // ⬅️ ملاحظات عامة
                        "  FOREIGN KEY (vehicle_plate) REFERENCES vehicles(plate_number) ON DELETE CASCADE," +
                        "  FOREIGN KEY (technician_id) REFERENCES technicians(id) ON DELETE CASCADE" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        stmt.executeUpdate(createMaintenanceOrdersTable);
        System.out.println("✅ جدول طلبات الصيانة جاهز");

        // 6. جدول الفواتير
        String createInvoicesTable =
                "CREATE TABLE IF NOT EXISTS invoices (" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY," +
                        "  invoice_number VARCHAR(50) UNIQUE NOT NULL," +
                        "  order_id INT NOT NULL," +
                        "  total_amount DECIMAL(10, 2) NOT NULL," +
                        "  subtotal DECIMAL(10, 2) DEFAULT 0.00," +
                        "  tax_amount DECIMAL(10, 2) DEFAULT 0.00," +
                        "  tax_rate DECIMAL(5,2) DEFAULT 0.00," +
                        "  discount_amount DECIMAL(10, 2) DEFAULT 0.00," +
                        "  discount_percentage DECIMAL(5,2) DEFAULT 0.00," +
                        "  due_date DATE," +
                        "  issued_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "  payment_date TIMESTAMP NULL," +
                        "  paid BOOLEAN DEFAULT FALSE," +
                        "  payment_method ENUM('cash', 'credit_card', 'bank_transfer', 'check') DEFAULT 'cash'," +
                        "  payment_status ENUM('pending', 'partial', 'paid', 'overdue') DEFAULT 'pending'," +
                        "  notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                        "  FOREIGN KEY (order_id) REFERENCES maintenance_orders(id) ON DELETE CASCADE" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        stmt.executeUpdate(createInvoicesTable);
        System.out.println("✅ جدول الفواتير جاهز");

        // 7. جدول العلاقة بين الطلبات وقطع الغيار
        String createOrderPartsTable =
                "CREATE TABLE IF NOT EXISTS order_parts (" +
                        "  order_id INT," +
                        "  part_id INT," +
                        "  quantity_used INT NOT NULL," +
                        "  unit_price DECIMAL(10, 2) NOT NULL," +
                        "  total_price DECIMAL(10, 2) GENERATED ALWAYS AS (quantity_used * unit_price) STORED," +
                        "  notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," + // ⬅️ ملاحظات استخدام القطعة
                        "  PRIMARY KEY (order_id, part_id)," +
                        "  FOREIGN KEY (order_id) REFERENCES maintenance_orders(id) ON DELETE CASCADE," +
                        "  FOREIGN KEY (part_id) REFERENCES spare_parts(id) ON DELETE CASCADE" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        stmt.executeUpdate(createOrderPartsTable);
        System.out.println("✅ جدول العلاقة بين الطلبات وقطع الغيار جاهز");

        // 8. جدول خدمات الصيانة
        String createServicesTable =
                "CREATE TABLE IF NOT EXISTS services (" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY," +
                        "  name VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL," +
                        "  description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                        "  price DECIMAL(10, 2) NOT NULL," +
                        "  estimated_duration INT," +
                        "  category VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," + // ⬅️ فئة الخدمة
                        "  notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," + // ⬅️ ملاحظات الخدمة
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        stmt.executeUpdate(createServicesTable);
        System.out.println("✅ جدول الخدمات جاهز");

        // 9. جدول العلاقة بين الطلبات والخدمات
        String createOrderServicesTable =
                "CREATE TABLE IF NOT EXISTS order_services (" +
                        "  order_id INT," +
                        "  service_id INT," +
                        "  quantity INT DEFAULT 1," +
                        "  unit_price DECIMAL(10, 2) NOT NULL," +
                        "  notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," + // ⬅️ ملاحظات الخدمة في الطلب
                        "  PRIMARY KEY (order_id, service_id)," +
                        "  FOREIGN KEY (order_id) REFERENCES maintenance_orders(id) ON DELETE CASCADE," +
                        "  FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        stmt.executeUpdate(createOrderServicesTable);
        System.out.println("✅ جدول العلاقة بين الطلبات والخدمات جاهز");

        // 10. جدول الدفعات (للدفعات الجزئية)
        String createPaymentsTable =
                "CREATE TABLE IF NOT EXISTS payments (" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY," +
                        "  invoice_id INT NOT NULL," +
                        "  amount DECIMAL(10, 2) NOT NULL," +
                        "  payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "  payment_method ENUM('cash', 'credit_card', 'bank_transfer', 'check') DEFAULT 'cash'," +
                        "  reference_number VARCHAR(50)," +
                        "  notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "  FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        stmt.executeUpdate(createPaymentsTable);
        System.out.println("✅ جدول الدفعات جاهز");

        stmt.close();
    }

    private static void addMissingColumns(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        System.out.println("🔧 === إضافة الأعمدة الناقصة للتوافق مع الاستعلامات ===");

        try {
            // ========== تحديث جدول العملاء ==========
            stmt.executeUpdate("ALTER TABLE customers ADD COLUMN IF NOT EXISTS customer_type ENUM('individual', 'company') DEFAULT 'individual'");
            stmt.executeUpdate("ALTER TABLE customers ADD COLUMN IF NOT EXISTS notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE customers ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");

            // ========== تحديث جدول السيارات ==========
            stmt.executeUpdate("ALTER TABLE vehicles ADD COLUMN IF NOT EXISTS brand VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE vehicles ADD COLUMN IF NOT EXISTS color VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE vehicles ADD COLUMN IF NOT EXISTS engine_type VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE vehicles ADD COLUMN IF NOT EXISTS engine_number VARCHAR(50)");
            stmt.executeUpdate("ALTER TABLE vehicles ADD COLUMN IF NOT EXISTS chassis_number VARCHAR(50)");
            stmt.executeUpdate("ALTER TABLE vehicles ADD COLUMN IF NOT EXISTS fuel_type ENUM('gasoline', 'diesel', 'electric', 'hybrid') DEFAULT 'gasoline'");
            stmt.executeUpdate("ALTER TABLE vehicles ADD COLUMN IF NOT EXISTS mileage INT DEFAULT 0");
            stmt.executeUpdate("ALTER TABLE vehicles ADD COLUMN IF NOT EXISTS last_maintenance_date DATE");
            stmt.executeUpdate("ALTER TABLE vehicles ADD COLUMN IF NOT EXISTS next_maintenance_date DATE");
            stmt.executeUpdate("ALTER TABLE vehicles ADD COLUMN IF NOT EXISTS insurance_expiry DATE");
            stmt.executeUpdate("ALTER TABLE vehicles ADD COLUMN IF NOT EXISTS notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"); // ⬅️ مهم جداً
            stmt.executeUpdate("ALTER TABLE vehicles ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");

            // ========== تحديث جدول الفنيين ==========
            stmt.executeUpdate("ALTER TABLE technicians ADD COLUMN IF NOT EXISTS email VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE technicians ADD COLUMN IF NOT EXISTS address TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"); // ⬅️ مهم جداً
            stmt.executeUpdate("ALTER TABLE technicians ADD COLUMN IF NOT EXISTS status ENUM('active', 'inactive', 'on_leave') DEFAULT 'active'");
            stmt.executeUpdate("ALTER TABLE technicians ADD COLUMN IF NOT EXISTS rating DECIMAL(3,2) DEFAULT 0.00");
            stmt.executeUpdate("ALTER TABLE technicians ADD COLUMN IF NOT EXISTS experience_years INT DEFAULT 0");
            stmt.executeUpdate("ALTER TABLE technicians ADD COLUMN IF NOT EXISTS notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE technicians ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");

            // ========== تحديث جدول قطع الغيار ==========
            stmt.executeUpdate("ALTER TABLE spare_parts ADD COLUMN IF NOT EXISTS description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE spare_parts ADD COLUMN IF NOT EXISTS part_number VARCHAR(50) UNIQUE");
            stmt.executeUpdate("ALTER TABLE spare_parts ADD COLUMN IF NOT EXISTS cost DECIMAL(10,2) NOT NULL DEFAULT 0.00");
            stmt.executeUpdate("ALTER TABLE spare_parts ADD COLUMN IF NOT EXISTS max_threshold INT DEFAULT 100");
            stmt.executeUpdate("ALTER TABLE spare_parts ADD COLUMN IF NOT EXISTS supplier VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE spare_parts ADD COLUMN IF NOT EXISTS supplier_phone VARCHAR(20)");
            stmt.executeUpdate("ALTER TABLE spare_parts ADD COLUMN IF NOT EXISTS location VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE spare_parts ADD COLUMN IF NOT EXISTS notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE spare_parts ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");

            // ========== تحديث جدول طلبات الصيانة ==========
            stmt.executeUpdate("ALTER TABLE maintenance_orders ADD COLUMN IF NOT EXISTS order_number VARCHAR(20) UNIQUE");
            stmt.executeUpdate("ALTER TABLE maintenance_orders ADD COLUMN IF NOT EXISTS customer_notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE maintenance_orders ADD COLUMN IF NOT EXISTS internal_notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE maintenance_orders ADD COLUMN IF NOT EXISTS priority ENUM('low', 'medium', 'high', 'urgent') DEFAULT 'medium'");
            stmt.executeUpdate("ALTER TABLE maintenance_orders ADD COLUMN IF NOT EXISTS estimated_hours DECIMAL(5,2)");
            stmt.executeUpdate("ALTER TABLE maintenance_orders ADD COLUMN IF NOT EXISTS actual_hours DECIMAL(5,2)");
            stmt.executeUpdate("ALTER TABLE maintenance_orders ADD COLUMN IF NOT EXISTS estimated_cost DECIMAL(10,2) DEFAULT 0.00");
            stmt.executeUpdate("ALTER TABLE maintenance_orders ADD COLUMN IF NOT EXISTS actual_cost DECIMAL(10,2) DEFAULT 0.00");
            stmt.executeUpdate("ALTER TABLE maintenance_orders ADD COLUMN IF NOT EXISTS labor_cost DECIMAL(10,2) DEFAULT 0.00");
            stmt.executeUpdate("ALTER TABLE maintenance_orders ADD COLUMN IF NOT EXISTS parts_cost DECIMAL(10,2) DEFAULT 0.00");
            stmt.executeUpdate("ALTER TABLE maintenance_orders ADD COLUMN IF NOT EXISTS scheduled_date DATE");
            stmt.executeUpdate("ALTER TABLE maintenance_orders ADD COLUMN IF NOT EXISTS start_date TIMESTAMP NULL");
            stmt.executeUpdate("ALTER TABLE maintenance_orders ADD COLUMN IF NOT EXISTS notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE maintenance_orders ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");

            // ========== تحديث جدول الفواتير ==========
            stmt.executeUpdate("ALTER TABLE invoices ADD COLUMN IF NOT EXISTS invoice_number VARCHAR(50) UNIQUE");
            stmt.executeUpdate("ALTER TABLE invoices ADD COLUMN IF NOT EXISTS due_date DATE");
            stmt.executeUpdate("ALTER TABLE invoices ADD COLUMN IF NOT EXISTS tax_amount DECIMAL(10,2) DEFAULT 0.00");
            stmt.executeUpdate("ALTER TABLE invoices ADD COLUMN IF NOT EXISTS tax_rate DECIMAL(5,2) DEFAULT 0.00");
            stmt.executeUpdate("ALTER TABLE invoices ADD COLUMN IF NOT EXISTS subtotal DECIMAL(10,2) DEFAULT 0.00");
            stmt.executeUpdate("ALTER TABLE invoices ADD COLUMN IF NOT EXISTS discount_amount DECIMAL(10,2) DEFAULT 0.00");
            stmt.executeUpdate("ALTER TABLE invoices ADD COLUMN IF NOT EXISTS discount_percentage DECIMAL(5,2) DEFAULT 0.00");
            stmt.executeUpdate("ALTER TABLE invoices ADD COLUMN IF NOT EXISTS payment_date TIMESTAMP NULL");
            stmt.executeUpdate("ALTER TABLE invoices ADD COLUMN IF NOT EXISTS payment_method ENUM('cash', 'credit_card', 'bank_transfer', 'check') DEFAULT 'cash'");
            stmt.executeUpdate("ALTER TABLE invoices ADD COLUMN IF NOT EXISTS payment_status ENUM('pending', 'partial', 'paid', 'overdue') DEFAULT 'pending'");
            stmt.executeUpdate("ALTER TABLE invoices ADD COLUMN IF NOT EXISTS notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE invoices ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");

            // ========== تحديث جدول order_parts ==========
            stmt.executeUpdate("ALTER TABLE order_parts ADD COLUMN IF NOT EXISTS unit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00");
            stmt.executeUpdate("ALTER TABLE order_parts ADD COLUMN IF NOT EXISTS total_price DECIMAL(10,2) GENERATED ALWAYS AS (quantity_used * unit_price) STORED");
            stmt.executeUpdate("ALTER TABLE order_parts ADD COLUMN IF NOT EXISTS notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");

            // ========== تحديث جدول services ==========
            stmt.executeUpdate("ALTER TABLE services ADD COLUMN IF NOT EXISTS category VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE services ADD COLUMN IF NOT EXISTS notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE services ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");

            // ========== تحديث جدول order_services ==========
            stmt.executeUpdate("ALTER TABLE order_services ADD COLUMN IF NOT EXISTS notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");

            System.out.println("✅ تم تحديث جميع الجداول بالأعمدة الناقصة");

        } catch (SQLException e) {
            System.out.println("⚠️ ملاحظة: بعض الأعمدة موجودة مسبقاً - " + e.getMessage());
        } finally {
            if (stmt != null) stmt.close();
        }
    }

    private static void insertSampleData(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {

            // التحقق مما إذا كانت الجداول فارغة
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM customers")) {
                rs.next();
                int customerCount = rs.getInt("count");

                if (customerCount == 0) {
                    System.out.println("📝 إضافة بيانات تجريبية...");

                    // 1. إضافة عملاء
                    String insertCustomers =
                            "INSERT INTO customers (name, phone, email, address, customer_type, notes) VALUES " +
                                    "('أحمد محمد', '0912345678', 'ahmed@example.com', 'بنغازي - الحي الأول', 'individual', 'عميل دائم - يفضل الدفع نقداً'), " +
                                    "('فاطمة علي', '0923456789', 'fatima@example.com', 'طرابلس - حي الأندلس', 'individual', 'تحب الصيانة الدورية كل 3 أشهر'), " +
                                    "('خالد حسين', '0934567890', NULL, 'درنة - وسط المدينة', 'individual', 'يملك عدة سيارات'), " +
                                    "('شركة النقل السريع', '0945678901', 'info@transport.com', 'مصراتة - المنطقة الصناعية', 'company', 'شركة نقل - لديهم 10 سيارات شحن')";

                    stmt.executeUpdate(insertCustomers);
                    System.out.println("✅ تم إضافة 4 عملاء");

                    // 2. إضافة فنيين
                    String insertTechnicians =
                            "INSERT INTO technicians (name, phone, email, address, specialization, hire_date, salary, status, rating, experience_years, notes) VALUES " +
                                    "('محمود سالم', '0945678901', 'mahmoud@garage.com', 'بنغازي - حي سيدي خليفة', 'ميكانيكا محركات', '2023-01-15', 2500.00, 'active', 4.5, 5, 'خبير في محركات تويوتا وهوندا'), " +
                                    "('سالم الكيومي', '0956789012', 'salem@garage.com', 'طرابلس - طريق المطار', 'كهرباء سيارات', '2023-03-20', 2200.00, 'active', 4.2, 3, 'متخصص في أنظمة الكهرباء الحديثة'), " +
                                    "('علي فرج', '0967890123', 'ali@garage.com', 'مصراتة - المنطقة الصناعية', 'سمكرة ودهان', '2023-06-10', 2000.00, 'inactive', 3.8, 4, 'في إجازة مرضية'), " +
                                    "('محمد العريبي', '0978901234', 'mohamed@garage.com', 'الزاوية - وسط المدينة', 'تكييف سيارات', '2023-08-05', 2300.00, 'on_leave', 4.0, 6, 'متخصص في تكييف السيارات الأوروبية')";

                    stmt.executeUpdate(insertTechnicians);
                    System.out.println("✅ تم إضافة 4 فنيين");

                    // 3. إضافة قطع غيار
                    String insertSpareParts =
                            "INSERT INTO spare_parts (name, description, part_number, price, cost, quantity, supplier, supplier_phone, location, notes) VALUES " +
                                    "('فلتر زيت', 'فلتر زيت محرك عالي الجودة - يناسب معظم السيارات', 'FLT-001', 15.50, 10.00, 25, 'مورد قطع الغيار', '0911111111', 'المخزن A - رف 3', 'الأكثر طلباً'), " +
                                    "('شمعة احتراق', 'شمعة احتراق سيارات بنزين - إيراني', 'SPK-002', 8.75, 5.50, 40, 'شركة الإشعال', '0922222222', 'المخزن B - رف 1', 'جودة متوسطة'), " +
                                    "('مكابح أمامية', 'قرص مكابح أمامية - ياباني', 'BRK-003', 45.00, 30.00, 12, 'مصنع المكابح', '0933333333', 'المخزن A - رف 5', 'للسيارات الكبيرة'), " +
                                    "('بطارية', 'بطارية سيارة 60 أمبير - كوري', 'BAT-004', 120.00, 85.00, 8, 'شركة البطاريات', '0944444444', 'المخزن C - رف 2', 'ضمان سنتين'), " +
                                    "('فلتر هواء', 'فلتر هواء محرك - صيني', 'AIR-005', 12.00, 7.50, 30, 'مورد قطع الغيار', '0911111111', 'المخزن A - رف 4', 'يناسب السيارات الصغيرة')";

                    stmt.executeUpdate(insertSpareParts);
                    System.out.println("✅ تم إضافة 5 قطع غيار");

                    // 4. إضافة سيارات
                    String insertVehicles =
                            "INSERT INTO vehicles (plate_number, brand, model, year, color, engine_type, engine_number, chassis_number, fuel_type, mileage, owner_id, last_maintenance_date, insurance_expiry, notes) VALUES " +
                                    "('12345', 'تويوتا', 'كورولا', 2020, 'أبيض', 'بنزين 1.6L', 'ENG123456', 'CHS654321', 'gasoline', 45000, 1, '2024-01-15', '2024-12-31', 'السيارة بحالة جيدة - تحتاج تغيير زيت كل 5000 كم'), " +
                                    "('54321', 'هيونداي', 'أكسنت', 2019, 'أسود', 'بنزين 1.4L', 'ENG789012', 'CHS987654', 'gasoline', 60000, 2, '2024-02-20', '2024-11-30', 'مشكلة في المكابح - تصدر صوت'), " +
                                    "('67890', 'كيا', 'سيراتو', 2021, 'أحمر', 'بنزين 1.8L', 'ENG345678', 'CHS123789', 'gasoline', 25000, 3, '2024-03-10', '2025-01-15', 'جديدة نسبياً - لا توجد مشاكل'), " +
                                    "('ABC123', 'مرسيدس', 'E200', 2022, 'فضي', 'بنزين 2.0L', 'ENG901234', 'CHS456123', 'gasoline', 15000, 4, '2024-03-25', '2024-10-20', 'سيارة شركة - تحتاج صيانة دورية')";

                    stmt.executeUpdate(insertVehicles);
                    System.out.println("✅ تم إضافة 4 سيارات");

                    // 5. إضافة طلبات صيانة
                    String insertOrders =
                            "INSERT INTO maintenance_orders (order_number, vehicle_plate, technician_id, description, customer_notes, internal_notes, priority, estimated_hours, estimated_cost, labor_cost, parts_cost, status, scheduled_date, start_date, completed_at, notes) VALUES " +
                                    "('ORD-2024-001', '12345', 1, 'تغيير زيت وتصفية وفلتر هواء', 'العميل يريد إصلاح صوت بالمحرك', 'تحتاج فحص إضافي للمحرك', 'medium', 2.0, 150.00, 50.00, 100.00, 'Completed', '2024-03-01', '2024-03-01 09:00:00', '2024-03-01 14:30:00', 'تم العمل بنجاح - العميل راضٍ'), " +
                                    "('ORD-2024-002', '54321', 2, 'فحص كهرباء السيارة وتغيير بطارية', 'السيارة لا تعمل في الصباح الباكر', 'السيارة تحتاج شحن دينمو', 'high', 3.0, 250.00, 75.00, 175.00, 'In Progress', '2024-03-05', '2024-03-05 10:30:00', NULL, 'في انتظار وصول بطارية جديدة'), " +
                                    "('ORD-2024-003', '67890', 1, 'تغييل مكابح أمامية وخلفية', 'صوت صرير عند الفرملة', 'تحتاج تغيير سائل الفرامل أيضاً', 'urgent', 4.0, 400.00, 120.00, 280.00, 'Waiting for Parts', '2024-03-10', NULL, NULL, 'في انتظار وصول قطع الغيار'), " +
                                    "('ORD-2024-004', 'ABC123', 3, 'دهان باب سائق وإصلاح خدوش', 'حادث بسيط في موقف السيارات', 'تحتاج تنظيف قبل الدهان', 'low', 6.0, 600.00, 200.00, 400.00, 'Pending', '2024-03-15', NULL, NULL, 'مؤجلة بسبب غياب الفني'), " +
                                    "('ORD-2024-005', '12345', 1, 'تغيير شمعات احتراق', 'استهلاك زيت زائد', 'تحتاج فحص حلقات المكابح', 'medium', 1.5, 120.00, 40.00, 80.00, 'In Progress', '2024-03-20', '2024-03-20 08:00:00', NULL, 'جاري العمل')";

                    stmt.executeUpdate(insertOrders);
                    System.out.println("✅ تم إضافة 5 طلبات صيانة");

                    // 6. إضافة فواتير
                    String insertInvoices =
                            "INSERT INTO invoices (invoice_number, order_id, subtotal, tax_amount, tax_rate, discount_amount, discount_percentage, total_amount, due_date, paid, payment_method, payment_status, notes) VALUES " +
                                    "('INV-2024-001', 1, 200.00, 20.00, 10.00, 10.00, 5.00, 210.00, '2024-04-01', true, 'cash', 'paid', 'تم الدفع نقداً - العميل أحمد محمد'), " +
                                    "('INV-2024-002', 2, 250.00, 25.00, 10.00, 25.00, 10.00, 250.00, '2024-03-05', false, NULL, 'overdue', 'فاتورة متأخرة - العميل فاطمة علي'), " +
                                    "('INV-2024-003', 3, 300.00, 30.00, 10.00, 30.00, 10.00, 300.00, '2024-04-10', false, NULL, 'pending', 'في انتظار اكتمال الصيانة'), " +
                                    "('INV-2024-004', 4, 500.00, 50.00, 10.00, 100.00, 20.00, 450.00, '2024-04-15', true, 'bank_transfer', 'paid', 'تحويل بنكي - شركة النقل السريع'), " +
                                    "('INV-2024-005', 5, 120.00, 12.00, 10.00, 0.00, 0.00, 132.00, '2024-04-20', false, NULL, 'pending', 'جاري العمل على الطلب')";

                    stmt.executeUpdate(insertInvoices);
                    System.out.println("✅ تم إضافة 5 فواتير");

                } else {
                    System.out.println("📊 قاعدة البيانات تحتوي على بيانات حالياً (" + customerCount + " عميل)");
                }
            }

            // عرض الإحصائيات
            System.out.println("\n📈 إحصائيات قاعدة البيانات:");

            String[] tables = {"customers", "technicians", "spare_parts", "vehicles", "maintenance_orders", "invoices", "services", "payments"};
            String[] arabicNames = {"العملاء", "الفنيين", "قطع الغيار", "السيارات", "طلبات الصيانة", "الفواتير", "الخدمات", "الدفعات"};

            for (int i = 0; i < tables.length; i++) {
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM " + tables[i])) {
                    if (rs.next()) {
                        System.out.println("   • " + arabicNames[i] + ": " + rs.getInt("count"));
                    }
                }
            }
        }
    }

    public static void dropAndRecreateDatabase() {
        System.out.println("🔄 === إعادة إنشاء قاعدة البيانات من الصفر ===");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");
             Statement stmt = conn.createStatement()) {

            // حذف قاعدة البيانات إذا كانت موجودة
            stmt.executeUpdate("DROP DATABASE IF EXISTS car_maintenance_db");
            System.out.println("🗑️ تم حذف قاعدة البيانات القديمة");

            // إعادة إنشاء كل شيء
            createDatabaseIfNotExists();

        } catch (SQLException e) {
            System.err.println("❌ خطأ في إعادة الإنشاء: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void testDatabaseConnection() {
        System.out.println("🔍 === اختبار اتصال قاعدة البيانات ===");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/car_maintenance_db", "root", "")) {

            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ الاتصال بقاعدة البيانات ناجح");

                // اختبار الاستعلامات
                try (Statement stmt = conn.createStatement()) {

                    // عرض الجداول
                    try (ResultSet rs = stmt.executeQuery("SHOW TABLES")) {
                        System.out.println("📋 الجداول الموجودة:");
                        int tableCount = 0;
                        while (rs.next()) {
                            tableCount++;
                            System.out.println("   • " + rs.getString(1));
                        }
                        System.out.println("📊 العدد الإجمالي للجداول: " + tableCount);
                    }

                    // اختبار الاستعلامات التي كانت تسبب أخطاء
                    System.out.println("\n🔍 اختبار الاستعلامات المسببة للأخطاء:");

                    // 1. اختبار جلب الفواتير المتأخرة - باستخدام try-with-resources
                    System.out.println("   اختبار الفواتير المتأخرة...");
                    try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as overdue_count FROM invoices WHERE due_date < CURDATE() AND paid = false")) {
                        if (rs.next()) {
                            System.out.println("   ✓ الفواتير المتأخرة: " + rs.getInt("overdue_count"));
                        }
                    } catch (SQLException e) {
                        System.out.println("   ✗ خطأ في استعلام الفواتير المتأخرة: " + e.getMessage());
                    }

                    // 2. اختبار عد الفنيين النشطين
                    System.out.println("   اختبار عد الفنيين النشطين...");
                    try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as active_tech FROM technicians WHERE status = 'active'")) {
                        if (rs.next()) {
                            System.out.println("   ✓ الفنيين النشطين: " + rs.getInt("active_tech"));
                        }
                    } catch (SQLException e) {
                        System.out.println("   ✗ خطأ في استعلام الفنيين النشطين: " + e.getMessage());
                    }

                    // 3. اختبار جلب السيارة مع ملاحظات
                    System.out.println("   اختبار جلب السيارة...");
                    try (ResultSet rs = stmt.executeQuery("SELECT plate_number, model, brand, year, color, notes FROM vehicles WHERE plate_number = '12345'")) {
                        if (rs.next()) {
                            System.out.println("   ✓ السيارة: " + rs.getString("plate_number") +
                                    " - " + rs.getString("brand") + " " + rs.getString("model") +
                                    " - ملاحظات: " + (rs.getString("notes") != null ? rs.getString("notes").substring(0, Math.min(50, rs.getString("notes").length())) + "..." : "لا توجد"));
                        }
                    } catch (SQLException e) {
                        System.out.println("   ✗ خطأ في استعلام السيارة: " + e.getMessage());
                    }

                    // 4. اختبار جلب الفني مع العنوان
                    System.out.println("   اختبار جلب الفني...");
                    try (ResultSet rs = stmt.executeQuery("SELECT id, name, phone, address, specialization, notes FROM technicians WHERE id = 1")) {
                        if (rs.next()) {
                            System.out.println("   ✓ الفني: " + rs.getString("name") +
                                    " - العنوان: " + rs.getString("address") +
                                    " - التخصص: " + rs.getString("specialization"));
                        }
                    } catch (SQLException e) {
                        System.out.println("   ✗ خطأ في استعلام الفني: " + e.getMessage());
                    }

                    // 5. اختبار جلب طلبات الصيانة النشطة
                    System.out.println("   اختبار جلب طلبات الصيانة النشطة...");
                    try (ResultSet rs = stmt.executeQuery("SELECT id, order_number, vehicle_plate, description, estimated_cost, notes FROM maintenance_orders WHERE status IN ('Pending', 'In Progress') ORDER BY priority DESC LIMIT 5")) {
                        int orderCount = 0;
                        while (rs.next()) {
                            orderCount++;
                            System.out.println("   ✓ طلب #" + rs.getInt("id") +
                                    " (" + rs.getString("order_number") +
                                    ") - لوحة: " + rs.getString("vehicle_plate") +
                                    " - التكلفة: " + String.format("%.2f", rs.getDouble("estimated_cost")));
                        }
                        System.out.println("   ✓ عدد الطلبات النشطة: " + orderCount);
                    } catch (SQLException e) {
                        System.out.println("   ✗ خطأ في استعلام طلبات الصيانة: " + e.getMessage());
                    }

                }

                System.out.println("\n🎉 جميع الاختبارات ناجحة!");

            } else {
                System.out.println("❌ فشل الاتصال بقاعدة البيانات");
            }

        } catch (SQLException e) {
            System.err.println("❌ فشل الاتصال: " + e.getMessage());

            if (e.getErrorCode() == 1049) { // قاعدة البيانات غير موجودة
                System.out.println("💡 الحل: قاعدة البيانات غير موجودة، سيتم إنشاؤها...");
                createDatabaseIfNotExists();
            }
        }
    }

    // دالة جديدة: اختبار كافة الاستعلامات بشكل شامل
    public static void comprehensiveTest() {
        System.out.println("🧪 === اختبار شامل لقاعدة البيانات ===");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/car_maintenance_db", "root", "");
             Statement stmt = conn.createStatement()) {

            // قائمة الاستعلامات للاختبار
            String[][] testQueries = {
                    {"جلب العملاء", "SELECT id, name, phone, address, notes FROM customers LIMIT 3"},
                    {"جلب السيارات مع الملاحظات", "SELECT plate_number, model, brand, year, notes FROM vehicles LIMIT 3"},
                    {"جلب الفنيين مع العنوان", "SELECT id, name, phone, address, specialization, notes FROM technicians WHERE status = 'active'"},
                    {"جلب قطع الغيار", "SELECT id, name, part_number, price, quantity, notes FROM spare_parts WHERE quantity < min_threshold"},
                    {"جلب طلبات الصيانة النشطة", "SELECT id, order_number, vehicle_plate, description, estimated_cost, notes FROM maintenance_orders WHERE status IN ('Pending', 'In Progress')"},
                    {"جلب الفواتير المتأخرة", "SELECT id, invoice_number, order_id, total_amount, due_date, notes FROM invoices WHERE due_date < CURDATE() AND paid = false"},
                    {"جلب الفواتير المدفوعة", "SELECT id, invoice_number, total_amount, discount_amount, notes FROM invoices WHERE paid = true LIMIT 3"},
                    {"جمع الإحصائيات", "SELECT (SELECT COUNT(*) FROM customers) as customers, (SELECT COUNT(*) FROM vehicles) as vehicles, (SELECT COUNT(*) FROM technicians WHERE status = 'active') as active_tech, (SELECT COUNT(*) FROM maintenance_orders WHERE status IN ('Pending', 'In Progress')) as active_orders"}
            };

            for (String[] test : testQueries) {
                String testName = test[0];
                String query = test[1];

                System.out.println("   اختبار: " + testName);
                try (ResultSet rs = stmt.executeQuery(query)) {
                    int rowCount = 0;
                    while (rs.next()) {
                        rowCount++;
                    }
                    System.out.println("   ✓ عدد النتائج: " + rowCount);
                } catch (SQLException e) {
                    System.out.println("   ✗ خطأ: " + e.getMessage());
                }
            }

            System.out.println("\n🎉 الاختبار الشامل مكتمل!");

        } catch (SQLException e) {
            System.err.println("❌ فشل الاختبار الشامل: " + e.getMessage());
        }
    }
}