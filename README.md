# RetrofitExample – Demo Retrofit trong lập trình Android

Ứng dụng này dùng cho bài seminar **"Ứng dụng Retrofit trong lập trình Android"**. App minh họa cách dùng Retrofit để gọi REST API (MockAPI) và thực hiện đầy đủ các thao tác **CRUD** trên danh sách người dùng (User).

## 1. Chức năng chính

- **Xem danh sách user** (GET `/users/`)
  - Màn hình chính `MainActivity` hiển thị ListView danh sách user.
  - Mỗi item:
    - Dòng 1: Họ tên user.
    - Dòng 2: `ID: <id>`.
    - Bên trái là ảnh avatar (nếu API có), dùng Picasso để load.

- **Xem chi tiết / chỉnh sửa / xóa user** (GET + PUT + DELETE)
  - Chọn 1 item trong danh sách → mở `UserEditActivity`.
  - Màn chi tiết hiển thị:
    - Name
    - ID (chỉ đọc)
    - Phone
    - Email
  - Nút **Save** (Edit):
    - Gửi request `PUT users/{id}/` để cập nhật thông tin user.
  - Nút **Delete**:
    - Gửi request `DELETE users/{id}/` để xóa user khỏi server.
  - Trên thanh ActionBar có nút **X** để quay lại màn hình danh sách.

- **Thêm user mới bằng form** (POST `/users/`)
  - Nút **ADD RANDOM USER** trên màn hình chính được dùng lại như nút **Add user**.
  - Khi bấm sẽ mở `AddUserActivity`:
    - Cho phép nhập: Name, Phone, Email.
    - Bấm **Save** → gửi request `POST users/` với body là object `User` mới.
  - Sau khi thêm thành công, màn hình quay lại `MainActivity` và danh sách được load lại.

## 2. Kiến trúc & các lớp quan trọng

### 2.1. Cấu hình Retrofit – `RetrofitClientInstance`

```java
public class RetrofitClientInstance {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://691cd7bd3aaeed735c924ffe.mockapi.io/api/";

    public static Retrofit getClient(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient())
                    .build();
        }
        return retrofit;
    }
}
```

- `BASE_URL` trỏ đến MockAPI.
- `GsonConverterFactory` tự động parse JSON ↔ object Java.
- `OkHttpClient` là HTTP client mà Retrofit sử dụng bên dưới.

### 2.2. Định nghĩa API – `ApiInterface`

```java
public interface ApiInterface {

    @GET("users/")
    Call<List<User>> getAllUsers();

    @PUT("users/{id}/")
    Call<User> setUserById(@Path("id") int id, @Body User user);

    @DELETE("users/{id}/")
    Call<User> deleteUserById(@Path("id") int id);

    @POST("users/")
    Call<User> addUser(@Body User user);
}
```

- Sử dụng annotation của Retrofit để map hàm Java thành HTTP request:
  - `@GET` / `@POST` / `@PUT` / `@DELETE`.
  - `@Path("id")` gắn tham số vào URL.
  - `@Body` gửi object `User` làm JSON trong body.

### 2.3. Model dữ liệu – `User`

- Ánh xạ JSON từ API:
  - `id` (Integer)
  - `createdAt` (String – nếu API có)
  - `name` (String)
  - `avatar` (String – URL ảnh)
  - `email` (String)
  - `phone` (Integer)
- Có đầy đủ getter/setter để sử dụng trong UI và gửi lên server.

### 2.4. Màn hình danh sách – `MainActivity`

- Khởi tạo Retrofit API:

```java
api = RetrofitClientInstance.getClient().create(ApiInterface.class);
```

- Lấy danh sách user (GET):

```java
Call<List<User>> listCall = api.getAllUsers();
listCall.enqueue(new Callback<List<User>>() {
    @Override
    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
        if (response.isSuccessful() && response.body() != null) {
            users.clear();
            users.addAll(response.body());
            listViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFailure(Call<List<User>> call, Throwable t) {
        Toast.makeText(MainActivity.this,
                "Failed to load users: " + t.getMessage(),
                Toast.LENGTH_SHORT).show();
    }
});
```

- Xử lý click item để mở màn hình chi tiết:

```java
listView.setOnItemClickListener((adapterView, view, position, l) -> {
    User user = users.get(position);
    Intent i = new Intent(MainActivity.this, UserEditActivity.class);
    Bundle bundle = new Bundle();
    bundle.putSerializable("user", user);
    i.putExtras(bundle);
    startActivity(i);
});
```

- Nút **Add user**:

```java
btn.setOnClickListener(view -> {
    Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
    startActivity(intent);
});
```

### 2.5. Màn hình chi tiết / sửa / xóa – `UserEditActivity`

- Nhận object `User` từ `Intent`, hiển thị Name, ID, Phone, Email.
- Nút Save gọi `setUserById()` (PUT) để cập nhật.
- Nút Delete gọi `deleteUserById()` (DELETE) để xóa.
- Có nút X trên ActionBar để quay lại `MainActivity`.

### 2.6. Màn hình thêm user – `AddUserActivity`

- Cho phép nhập Name, Phone, Email.
- Tạo object `User` mới rồi gọi `addUser()` (POST).
- Khi thêm thành công, hiển thị Toast và `finish()` để quay lại, `MainActivity.onResume()` sẽ load lại danh sách.

### 2.7. Adapter – `CustomListViewAdapter`

- Custom item cho ListView:
  - Load avatar bằng Picasso (nếu có URL), nếu không dùng icon mặc định.
  - Dòng 1: `user_name_label` = tên.
  - Dòng 2: `user_address_label` = `"ID: " + user.getId()`.

## 3. Cách chạy project

### 3.1. Yêu cầu

- Android Studio (bản mới, hỗ trợ Gradle của project).
- Thiết bị thật hoặc emulator có Internet.

### 3.2. Các bước

1. Clone project hoặc copy folder `android-retrofit-crud-example`.
2. Mở bằng Android Studio: `File > Open...` chọn thư mục project.
3. Đợi Gradle sync xong.
4. Chạy app:
   - Chọn thiết bị/emulator.
   - Bấm **Run**.

Hoặc dùng dòng lệnh (Windows, từ thư mục gốc project):

```bat
gradlew.bat assembleDebug
```

## 4. Gợi ý kịch bản demo seminar

1. **Giới thiệu tổng quan**
   - Retrofit là gì, vì sao dùng: đơn giản hóa việc gọi REST API, parse JSON tự động, dễ bảo trì.
   - App demo: quản lý danh sách user với CRUD.

2. **Trình bày kiến trúc** (mở code):
   - `RetrofitClientInstance` – cấu hình base URL, converter.
   - `ApiInterface` – định nghĩa API bằng annotation.
   - `User` – model dữ liệu, ánh xạ từ JSON.

3. **Demo GET – danh sách user**
   - Mở `MainActivity`, cho xem hàm `fillArrayList()`.
   - Chạy app, màn hình hiện danh sách user từ MockAPI.

4. **Demo PUT/DELETE – sửa & xóa user**
   - Bấm 1 user → `UserEditActivity`.
   - Sửa thông tin, lưu lại → danh sách cập nhật.
   - Thử xóa một user → item biến mất khỏi list.

5. **Demo POST – thêm user mới**
   - Bấm nút **Add user** trên `MainActivity`.
   - Nhập Name / Phone / Email, Save → user mới xuất hiện trong danh sách.

6. **Kết luận**
   - Retrofit giúp tách riêng phần gọi API (interface) khỏi UI.
   - Chỉ cần đổi `BASE_URL` hoặc thêm hàm trong `ApiInterface` là có thể mở rộng app.


