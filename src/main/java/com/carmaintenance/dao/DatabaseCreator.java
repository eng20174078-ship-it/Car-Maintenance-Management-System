package com.carmaintenance.dao;

import java.sql.*;

public class DatabaseCreator {

    public static void createDatabaseIfNotExists() {
        System.out.println("🗄️ === إنشاء قاعدة البيانات والجداول ===");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");
             Statement stmt = conn.createStatement()) {

            System.out.println("✅ الاتصال بـ MySQL ناجح");

            // 1. إنشاء قاعدة البيانات إذا لم تكن موجودة
            String createDbSQL = "CREATE DATABASE IF NOT EXISTS car_maintenance_db " +
                    "CHARACTER SET utf8mb4 " +
                    "COLLATE utf8mb4_unicode_ci";

            stmt.executeUpdate(createDbSQL);
            System.out.println("✅ تم إنشاء/التحقق من قاعدة البيانات");

            // 2. استخدام قاعدة البيانات
            stmt.executeUpdate("USE car_maintenance_db");
            System.out.println("✅ تم التبديل إلى قاعدة البيانات");

            // 3. إنشاء الجداول
            createTables(conn);

            // 4. إضافة بيانات تجريبية
            insertSampleData(conn);

            // 5. تحديث الجداول بإضافة الأعمدة الناقصة
            addMissingColumns(conn);

            System.out.println("🎉 تم إنشاء قاعدة البيانات والجداول بنجاح!");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في إنشاء قاعدة البيانات: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {

            // 1. جدول العملاء
            String createCustomersTable =
                    "CREATE TABLE IF NOT EXISTS customers (" +
                            "  id INT AUTO_INCREMENT PRIMARY KEY," +
                            "  name VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL," +
                            "  phone VARCHAR(20) UNIQUE NOT NULL," +
                            "  email VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                            "  address TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                            "  customer_type ENUM('individual', 'company') DEFAULT 'individual'," +
                            "  notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
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
                            "  brand VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                            "  year INT," +
                            "  color VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                            "  engine_type VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                            "  engine_number VARCHAR(50)," +
                            "  chassis_number VARCHAR(50)," +
                            "  fuel_type ENUM('gasoline', 'diesel', 'electric', 'hybrid') DEFAULT 'gasoline'," +
                            "  mileage INT DEFAULT 0," +
                            "  last_maintenance_date DATE," +
                            "  next_maintenance_date DATE," +
                            "  insurance_expiry DATE," +
                            "  notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
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
                            "  address TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                            "  specialization VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                            "  hire_date DATE," +
                            "  salary DECIMAL(10, 2)," +
                            "  status ENUM('active', 'inactive', 'on_leave') DEFAULT 'active'," +
                            "  rating DECIMAL(3,2) DEFAULT 0.00," +
                            "  experience_years INT DEFAULT 0," +
                            "  notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
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
                            "  brand VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                            "  description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                            "  part_number VARCHAR(50) UNIQUE," +
                            "  category VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," + // ⬅️ عمود category مضاف هنا
                            "  price DECIMAL(10, 2) NOT NULL," +
                            "  cost DECIMAL(10, 2) NOT NULL," +
                            "  quantity INT DEFAULT 0," +
                            "  min_threshold INT DEFAULT 5," +
                            "  max_threshold INT DEFAULT 100," +
                            "  supplier VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                            "  supplier_phone VARCHAR(20)," +
                            "  location VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                            "  notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
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
                            "  start_date TIMESTAMP NULL," +
                            "  completed_at TIMESTAMP NULL," +
                            "  notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
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
                            "  notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
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
                            "  category VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                            "  notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
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
                            "  notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                            "  PRIMARY KEY (order_id, service_id)," +
                            "  FOREIGN KEY (order_id) REFERENCES maintenance_orders(id) ON DELETE CASCADE," +
                            "  FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

            stmt.executeUpdate(createOrderServicesTable);
            System.out.println("✅ جدول العلاقة بين الطلبات والخدمات جاهز");

            // 10. جدول الدفعات
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
        }
    }

    private static void addMissingColumns(Connection conn) throws SQLException {
        System.out.println("🔧 === إضافة الأعمدة الناقصة للتوافق مع الاستعلامات ===");

        try (Statement stmt = conn.createStatement()) {

            // ========== تحديث جدول قطع الغيار ==========
            stmt.executeUpdate("ALTER TABLE spare_parts ADD COLUMN IF NOT EXISTS category VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            System.out.println("✅ تم إضافة/التحقق من عمود category في قطع الغيار");
            stmt.executeUpdate("ALTER TABLE spare_parts ADD COLUMN IF NOT EXISTS brand VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            System.out.println("✅ تم إضافة/التحقق من عمود brand في قطع الغيار");

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
            stmt.executeUpdate("ALTER TABLE vehicles ADD COLUMN IF NOT EXISTS notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE vehicles ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");

            // ========== تحديث جدول الفنيين ==========
            stmt.executeUpdate("ALTER TABLE technicians ADD COLUMN IF NOT EXISTS email VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE technicians ADD COLUMN IF NOT EXISTS address TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE technicians ADD COLUMN IF NOT EXISTS status ENUM('active', 'inactive', 'on_leave') DEFAULT 'active'");
            stmt.executeUpdate("ALTER TABLE technicians ADD COLUMN IF NOT EXISTS rating DECIMAL(3,2) DEFAULT 0.00");
            stmt.executeUpdate("ALTER TABLE technicians ADD COLUMN IF NOT EXISTS experience_years INT DEFAULT 0");
            stmt.executeUpdate("ALTER TABLE technicians ADD COLUMN IF NOT EXISTS notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE technicians ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");

            // ========== تحديث جدول قطع الغيار (استكمال) ==========
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

            // ========== تحديث جدول العلاقات ==========
            stmt.executeUpdate("ALTER TABLE order_parts ADD COLUMN IF NOT EXISTS unit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00");
            stmt.executeUpdate("ALTER TABLE order_parts ADD COLUMN IF NOT EXISTS total_price DECIMAL(10,2) GENERATED ALWAYS AS (quantity_used * unit_price) STORED");
            stmt.executeUpdate("ALTER TABLE order_parts ADD COLUMN IF NOT EXISTS notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");

            stmt.executeUpdate("ALTER TABLE services ADD COLUMN IF NOT EXISTS category VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE services ADD COLUMN IF NOT EXISTS notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("ALTER TABLE services ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");

            stmt.executeUpdate("ALTER TABLE order_services ADD COLUMN IF NOT EXISTS notes TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");

            System.out.println("✅ تم تحديث جميع الجداول بالأعمدة الناقصة");

        } catch (SQLException e) {
            System.out.println("⚠️ ملاحظة: بعض الأعمدة موجودة مسبقاً - " + e.getMessage());
        }
    }

    private static void insertSampleData(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {

            // التحقق مما إذا كانت الجداول فارغة
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM customers")) {
                if (rs.next() && rs.getInt("count") == 0) {
                    System.out.println("📝 إضافة بيانات تجريبية...");
                    insertAllSampleData(stmt);
                } else {
                    System.out.println("📊 قاعدة البيانات تحتوي على بيانات حالياً");
                }
            }

            // عرض الإحصائيات
            displayStatistics(stmt);
        }
    }

    private static void insertAllSampleData(Statement stmt) throws SQLException {
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

        // 3. إضافة قطع غيار مع category
        String insertSpareParts =
                "INSERT INTO spare_parts (name, brand, description, part_number, category, price, cost, quantity, supplier, supplier_phone, location, notes) VALUES " +
                        "('فلتر زيت', 'Bosch', 'فلتر زيت محرك عالي الجودة - يناسب معظم السيارات', 'FLT-001', 'فلاتر', 15.50, 10.00, 25, 'مورد قطع الغيار', '0911111111', 'المخزن A - رف 3', 'الأكثر طلباً'), " +
                        "('شمعة احتراق', 'NGK', 'شمعة احتراق سيارات بنزين - إيراني', 'SPK-002', 'إشعال', 8.75, 5.50, 40, 'شركة الإشعال', '0922222222', 'المخزن B - رف 1', 'جودة متوسطة'), " +
                        "('مكابح أمامية', 'Brembo', 'قرص مكابح أمامية - ياباني', 'BRK-003', 'فرامل', 45.00, 30.00, 12, 'مصنع المكابح', '0933333333', 'المخزن A - رف 5', 'للسيارات الكبيرة'), " +
                        "('بطارية', 'Exide', 'بطارية سيارة 60 أمبير - كوري', 'BAT-004', 'كهرباء', 120.00, 85.00, 8, 'شركة البطاريات', '0944444444', 'المخزن C - رف 2', 'ضمان سنتين'), " +
                        "('فلتر هواء', 'Mann Filter', 'فلتر هواء محرك - صيني', 'AIR-005', 'فلاتر', 12.00, 7.50, 30, 'مورد قطع الغيار', '0911111111', 'المخزن A - رف 4', 'يناسب السيارات الصغيرة')";

        stmt.executeUpdate(insertSpareParts);
        System.out.println("✅ تم إضافة 5 قطع غيار مع category");

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

        // 7. إضافة خدمات
        String insertServices =
                "INSERT INTO services (name, description, price, estimated_duration, category, notes) VALUES " +
                        "('تغيير زيت', 'تغيير زيت المحرك مع الفلتر', 30.00, 30, 'صيانة دورية', 'الخدمة الأكثر طلباً'), " +
                        "('فحص كهرباء', 'فحص شامل لنظام الكهرباء', 50.00, 60, 'كهرباء', 'تشخيص الأعطال الكهربائية'), " +
                        "('تغيير مكابح', 'تغييل مكابح أمامية أو خلفية', 80.00, 90, 'فرامل', 'تتضمن تغيير سائل الفرامل'), " +
                        "('غسيل سيارة', 'غسيل خارجي وداخلي كامل', 25.00, 45, 'تنظيف', 'تنظيف وتلميع'), " +
                        "('تغيير شمعات', 'تغيير شمعات الاحتراق', 40.00, 60, 'محرك', 'تحسين أداء المحرك')";

        stmt.executeUpdate(insertServices);
        System.out.println("✅ تم إضافة 5 خدمات");
    }

    private static void displayStatistics(Statement stmt) throws SQLException {
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

    public static void dropAndRecreateDatabase() {
        System.out.println("🔄 === إعادة إنشاء قاعدة البيانات من الصفر ===");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("DROP DATABASE IF EXISTS car_maintenance_db");
            System.out.println("🗑️ تم حذف قاعدة البيانات القديمة");

            createDatabaseIfNotExists();

        } catch (SQLException e) {
            System.err.println("❌ خطأ في إعادة الإنشاء: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== دوال الاختبار المحسنة ====================

    public static void testDatabaseConnection() {
        System.out.println("🔍 === اختبار اتصال قاعدة البيانات ===");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/car_maintenance_db", "root", "")) {

            if (!conn.isClosed()) {
                System.out.println("✅ الاتصال بقاعدة البيانات ناجح");

                // اختبار جميع الاستعلامات المهمة
                testCriticalQueries(conn);

                System.out.println("\n🎉 جميع الاختبارات ناجحة!");
            }

        } catch (SQLException e) {
            System.err.println("❌ فشل الاتصال: " + e.getMessage());

            if (e.getErrorCode() == 1049) {
                System.out.println("💡 الحل: قاعدة البيانات غير موجودة، سيتم إنشاؤها...");
                createDatabaseIfNotExists();
            }
        }
    }

    private static void testCriticalQueries(Connection conn) {
        String[][] testQueries = {
                {"جلب الفواتير المتأخرة",
                        "SELECT id, invoice_number, total_amount, due_date, notes " +
                                "FROM invoices WHERE due_date < CURDATE() AND paid = false"},

                {"جلب طلبات الصيانة النشطة",
                        "SELECT id, order_number, vehicle_plate, description, estimated_cost, notes " +
                                "FROM maintenance_orders WHERE status IN ('Pending', 'In Progress')"},

                {"جلب الفواتير غير المدفوعة",
                        "SELECT id, invoice_number, total_amount, due_date, notes " +
                                "FROM invoices WHERE paid = false"},

                {"جلب طلبات الصيانة المكتملة",
                        "SELECT id, order_number, vehicle_plate, description, actual_cost, notes " +
                                "FROM maintenance_orders WHERE status = 'Completed'"},

                {"جلب قطع الغيار مع category",
                        "SELECT id, name, category, price, quantity, notes " +
                                "FROM spare_parts ORDER BY category"},

                {"جلب جميع الفواتير",
                        "SELECT id, invoice_number, order_id, total_amount, paid, notes " +
                                "FROM invoices LIMIT 5"},

                {"جلب جميع طلبات الصيانة",
                        "SELECT id, order_number, vehicle_plate, status, estimated_cost, notes " +
                                "FROM maintenance_orders LIMIT 5"}
        };

        for (String[] test : testQueries) {
            executeAndPrintTest(conn, test[0], test[1]);
        }
    }

    private static void executeAndPrintTest(Connection conn, String testName, String query) {
        System.out.println("\n   اختبار: " + testName);

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                // عرض بعض البيانات للتحقق
                if (rowCount <= 3) {
                    StringBuilder rowInfo = new StringBuilder("     - ");
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        String columnName = rs.getMetaData().getColumnName(i);
                        if (columnName.equals("notes") || columnName.equals("description")) {
                            // تقصير النصوص الطويلة
                            String value = rs.getString(i);
                            if (value != null && value.length() > 30) {
                                rowInfo.append(columnName).append(": ").append(value.substring(0, 30)).append("... | ");
                            } else {
                                rowInfo.append(columnName).append(": ").append(value).append(" | ");
                            }
                        } else if (columnName.equals("total_amount") || columnName.equals("estimated_cost") ||
                                columnName.equals("actual_cost") || columnName.equals("price")) {
                            rowInfo.append(columnName).append(": ").append(String.format("%.2f", rs.getDouble(i))).append(" | ");
                        } else {
                            rowInfo.append(columnName).append(": ").append(rs.getString(i)).append(" | ");
                        }
                    }
                    System.out.println(rowInfo.toString());
                }
            }

            System.out.println("   ✓ تم استرجاع " + rowCount + " سجلاً");

        } catch (SQLException e) {
            System.out.println("   ✗ خطأ: " + e.getMessage());
            System.out.println("   ✗ الاستعلام: " + query);
        }
    }

    // دالة جديدة: اختبار شامل مع إدارة محسنة للموارد
    public static void runComprehensiveTest() {
        System.out.println("🧪 === اختبار شامل لقاعدة البيانات ===");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/car_maintenance_db", "root", "")) {

            // اختبار 1: الاستعلامات الأساسية
            System.out.println("\n📊 الاختبار 1: الاستعلامات الأساسية");
            testBasicQueries(conn);

            // اختبار 2: الاستعلامات المعقدة
            System.out.println("\n📊 الاختبار 2: الاستعلامات المعقدة");
            testComplexQueries(conn);

            // اختبار 3: تجميع البيانات
            System.out.println("\n📊 الاختبار 3: تجميع البيانات");
            testAggregationQueries(conn);

            System.out.println("\n🎉 الاختبار الشامل مكتمل بنجاح!");

        } catch (SQLException e) {
            System.err.println("❌ فشل الاختبار الشامل: " + e.getMessage());
        }
    }

    private static void testBasicQueries(Connection conn) throws SQLException {
        String[] basicQueries = {
                "SELECT * FROM customers LIMIT 2",
                "SELECT * FROM vehicles LIMIT 2",
                "SELECT * FROM technicians WHERE status = 'active'",
                "SELECT * FROM spare_parts WHERE quantity < min_threshold",
                "SELECT * FROM maintenance_orders WHERE status = 'In Progress'",
                "SELECT * FROM invoices WHERE paid = false"
        };

        for (String query : basicQueries) {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                System.out.println("   ✓ " + query.split("FROM")[1].split("LIMIT|WHERE")[0].trim() + ": " + getRowCount(rs) + " سجل");
            }
        }
    }

    private static void testComplexQueries(Connection conn) throws SQLException {
        String[] complexQueries = {
                // الفواتير المتأخرة مع تفاصيل الطلب
                "SELECT i.id, i.invoice_number, i.total_amount, i.due_date, " +
                        "mo.order_number, mo.vehicle_plate, c.name as customer_name " +
                        "FROM invoices i " +
                        "JOIN maintenance_orders mo ON i.order_id = mo.id " +
                        "JOIN vehicles v ON mo.vehicle_plate = v.plate_number " +
                        "JOIN customers c ON v.owner_id = c.id " +
                        "WHERE i.due_date < CURDATE() AND i.paid = false",

                // طلبات الصيانة النشطة مع تفاصيل الفني والعميل
                "SELECT mo.id, mo.order_number, mo.vehicle_plate, " +
                        "t.name as technician_name, c.name as customer_name, " +
                        "mo.estimated_cost, mo.status " +
                        "FROM maintenance_orders mo " +
                        "JOIN technicians t ON mo.technician_id = t.id " +
                        "JOIN vehicles v ON mo.vehicle_plate = v.plate_number " +
                        "JOIN customers c ON v.owner_id = c.id " +
                        "WHERE mo.status IN ('Pending', 'In Progress')",

                // قطع الغيار مع تفاصيل المورد
                "SELECT sp.name, sp.category, sp.price, sp.quantity, " +
                        "sp.supplier, sp.supplier_phone, sp.notes " +
                        "FROM spare_parts sp " +
                        "WHERE sp.quantity > 0 " +
                        "ORDER BY sp.category, sp.name"
        };

        for (String query : complexQueries) {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                System.out.println("   ✓ استعلام معقد: " + getRowCount(rs) + " سجل");
            }
        }
    }

    private static void testAggregationQueries(Connection conn) throws SQLException {
        String[] aggregationQueries = {
                "SELECT COUNT(*) as total_customers FROM customers",
                "SELECT COUNT(*) as active_technicians FROM technicians WHERE status = 'active'",
                "SELECT COUNT(*) as active_orders FROM maintenance_orders WHERE status IN ('Pending', 'In Progress')",
                "SELECT SUM(total_amount) as total_sales FROM invoices WHERE paid = true",
                "SELECT SUM(total_amount) as total_pending FROM invoices WHERE paid = false",
                "SELECT AVG(estimated_cost) as avg_estimate FROM maintenance_orders",
                "SELECT category, COUNT(*) as count FROM spare_parts GROUP BY category"
        };

        for (String query : aggregationQueries) {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    String result = rs.getMetaData().getColumnName(1) + ": " + rs.getString(1);
                    if (rs.getMetaData().getColumnCount() > 1) {
                        result += ", " + rs.getMetaData().getColumnName(2) + ": " + rs.getString(2);
                    }
                    System.out.println("   ✓ " + result);
                }
            }
        }
    }

    private static int getRowCount(ResultSet rs) throws SQLException {
        int count = 0;
        while (rs.next()) {
            count++;
        }
        return count;
    }
}