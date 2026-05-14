// 1. Chọn Database (Hãy đổi 'product_db' thành tên DB bạn khai báo trong application.properties)
db = db.getSiblingDB('database');

// 2. Làm sạch dữ liệu cũ trước khi nạp mới
db.products.drop();
db.users.drop();

// 3. Thêm dữ liệu vào Collection 'products' (Khớp với ProductDTO)
db.products.insertMany([
  {
    "name": "iPhone 15 Pro Max",
    "price": 3000.0,
    "description": "Chip A17 Pro mạnh mẽ, khung Titan siêu bền.",
    "image": "https://cdn2.cellphones.com.vn/358x/media/catalog/product/i/p/iphone15-pro-max-titan-trang.jpg",
    "checkToCart": false,
    "rating": 5,
    "quantity": 50,
    "productCode": "IP15PM"
  },
  {
    "name": "Samsung Galaxy S24 Ultra",
    "price": 2800.0,
    "description": "Tích hợp AI quyền năng, camera 200MP.",
    "image": "https://cdn2.cellphones.com.vn/358x/media/catalog/product/g/a/galaxy-s24-ultra-tim_1_3.png",
    "checkToCart": false,
    "rating": 5,
    "quantity": 40,
    "productCode": "S24U"
  },
  {
    "name": "Sony WH-1000XM5",
    "price": 700.0,
    "description": "Tai nghe chống ồn đỉnh cao, âm thanh Hi-Res.",
    "image": "https://cdn2.cellphones.com.vn/358x/media/catalog/product/t/a/tai-nghe-chup-tai-sony-wh-1000xm5-3_1.png",
    "checkToCart": false,
    "rating": 4,
    "quantity": 100,
    "productCode": "XM5"
  },
  {
    "name": "MacBook Pro M3",
    "price": 4500.0,
    "description": "Laptop cho lập trình viên chuyên nghiệp.",
    "image": "https://cdn2.cellphones.com.vn/x/media/catalog/product/m/a/macbook-pro-14-inch-m3-2023_9__1.png",
    "checkToCart": false,
    "rating": 5,
    "quantity": 25,
    "productCode": "MBPM3"
  },
  {
    "name": "Logitech MX Master 3S",
    "price": 250.0,
    "description": "Chuột công thái học, hỗ trợ làm việc đa thiết bị.",
    "image": "https://cdn2.cellphones.com.vn/x/media/catalog/product/1/_/1_346.jpg",
    "checkToCart": false,
    "rating": 5,
    "quantity": 80,
    "productCode": "MX3S"
  },
  {
    "name": "iPad Pro M4",
    "price": 2000.0,
    "description": "Màn hình OLED rực rỡ, siêu mỏng nhẹ.",
    "image": "https://cdn2.cellphones.com.vn/358x/media/catalog/product/f/r/frame_100_1_2__1.png",
    "checkToCart": false,
    "rating": 4,
    "quantity": 30,
    "productCode": "IPAD-M4"
  },
  {
    "name": "Keychron K2 V2",
    "price": 180.0,
    "description": "Bàn phím cơ không dây cho người mới bắt đầu.",
    "image": "https://owlgaming.vn/wp-content/uploads/2021/01/Keychron-K2-V2-vo-nhua.jpg",
    "checkToCart": false,
    "rating": 4,
    "quantity": 60,
    "productCode": "K2V2"
  },
  {
    "name": "Dell XPS 13 Plus",
    "price": 3200.0,
    "description": "Thiết kế tương lai, hiệu năng ổn định.",
    "image": "https://laptoptcc.com/media/product/33602-dell-xps-9320.jpg",
    "checkToCart": false,
    "rating": 4,
    "quantity": 15,
    "productCode": "XPS13P"
  },
  {
    "name": "Apple Watch Ultra 2",
    "price": 1200.0,
    "description": "Đồng hồ thể thao chuyên nghiệp.",
    "image": "https://cdn2.cellphones.com.vn/x/media/catalog/product/t/e/text_ng_n_25__7_5.png",
    "checkToCart": false,
    "rating": 5,
    "quantity": 45,
    "productCode": "AW-U2"
  },
  {
    "name": "Kindle Paperwhite 5",
    "price": 350.0,
    "description": "Máy đọc sách bảo vệ mắt tốt nhất.",
    "image": "https://kindlehanoi.vn/wp-content/uploads/2022/02/z3196853429126_4f3803fac037a3c5c77bd1152a510562-scaled.jpg",
    "checkToCart": false,
    "rating": 5,
    "quantity": 20,
    "productCode": "KPW5"
  }
]);

// 4. Thêm dữ liệu vào Collection 'users' (Khớp với UserDTO)
db.users.insertOne({
  "username": "admin",
  "email": "admin@gmail.com",
  "password": "password123", // Lưu ý: Password này nên trùng với format mã hóa trong code của bạn
  "isAdmin": true,
  "expirationDate": 30
});

print("--- [SUCCESS]: Khoa đã nạp 10 sản phẩm và 1 tài khoản Admin thành công! ---");