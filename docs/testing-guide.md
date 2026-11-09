# Testing Guide — PTIT PrepQuiz

> **Người phụ trách:** Lê Đăng Hiệp (Module 1: Quản lý Người dùng & Thống kê học tập)  
> **Ứng dụng:** Quiz App (Trắc nghiệm) — Android, Java, SQLite thuần, Offline-first

---

## 1. Mục tiêu kiểm thử (Test Objectives)

Tài liệu này hướng dẫn chiến lược kiểm thử cho ứng dụng **PTIT PrepQuiz**, bảo đảm:
1. **Toàn vẹn dữ liệu SQLite**: Kiểm tra CRUD câu hỏi, khôi phục câu hỏi mẫu không làm mất câu hỏi tự tạo, bảo toàn dữ liệu lịch sử thi.
2. **Khả năng chịu lỗi và trạng thái (State Persistence)**: Không bị crash, không mất tiến trình làm bài khi xoay màn hình (Orientation Change) hoặc khi app bị đưa xuống nền.
3. **Độ chính xác thuật toán**: Đảm bảo thuật toán chấm điểm (`ExamEngine`), xếp loại (`ScoreGrader`), và kiểm tra tính hợp lệ (`QuestionValidator`) hoạt động chính xác 100%.
4. **Trải nghiệm ngoại tuyến (100% Offline)**: Ứng dụng hoạt động mượt mà không cần kết nối Internet, không gửi request mạng trái phép.

---

## 2. Ma trận phân công kiểm thử theo 3 Module

| Module | Thành viên | Phạm vi kiểm thử chính | Các lớp Unit Test / UI Test |
|---|---|---|---|
| **Module 1: Thống kê & Lịch sử** | **Lê Đăng Hiệp** | - Thêm lịch sử thi vào SQLite<br>- Truy vấn danh sách lịch sử theo thứ tự mới nhất<br>- Tính toán điểm trung bình và định dạng thời gian | `HistoryRepositoryTest`, `TimeFormatterTest`, `HistoryActivityTest` |
| **Module 2: Ngân hàng câu hỏi** | **Lê Đức Mạnh** | - CRUD câu hỏi (Thêm/Sửa/Xóa)<br>- Tìm kiếm và lọc theo danh mục<br>- Khôi phục dữ liệu mẫu (Seed)<br>- Validate form nhập liệu | `QuestionValidatorTest`, `QuestionRepositoryTest`, `QuizDatabaseHelperTest` |
| **Module 3: Trải nghiệm thi (Exam Engine)** | **Đinh Đức Nghĩa** | - Sinh đề ngẫu nhiên theo số lượng và môn<br>- Đếm ngược đồng hồ và tự động nộp bài<br>- Xoay màn hình giữ nguyên đáp án đã chọn và cờ<br>- Chấm điểm và hiển thị kết quả chi tiết | `ExamEngineTest`, `ScoreGraderTest`, `ExamSessionTest`, `ExamActivityTest` |

---

## 3. Các kịch bản kiểm thử trọng yếu (Test Cases)

### 3.1. Unit Tests

#### TC-01: Kiểm tra tính hợp lệ của câu hỏi (`QuestionValidatorTest`)
- **Đầu vào:** Câu hỏi thiếu nội dung, hoặc thiếu một trong 4 đáp án A/B/C/D, hoặc `correctAnswer = 5`.
- **Kỳ vọng:** `validate()` trả về `isValid = false` kèm thông báo lỗi rõ ràng.

#### TC-02: Kiểm tra thuật toán chấm điểm (`ExamEngineTest`)
- **Đầu vào:** Đề thi gồm 10 câu hỏi, mảng đáp án chọn trùng khớp 8 câu, sai 1 câu, bỏ trống 1 câu.
- **Kỳ vọng:** 
  - `totalQuestions = 10`
  - `correctCount = 8`
  - `scorePercent = 80.0%`
  - Danh sách `details` có đúng trạng thái đúng/sai cho từng câu.

#### TC-03: Kiểm tra xếp loại bài thi (`ScoreGraderTest`)
- **Đầu vào:** Tỷ lệ phần trăm điểm:
  - `95.0%` -> Kỳ vọng: `"Xuất sắc"`
  - `75.0%` -> Kỳ vọng: `"Khá tốt"`
  - `50.0%` -> Kỳ vọng: `"Đạt yêu cầu"`
  - `40.0%` -> Kỳ vọng: `"Cần cố gắng"`

#### TC-04: Kiểm tra định dạng thời gian (`TimeFormatterTest`)
- **Đầu vào:** 
  - `0` giây -> `"00:00"`
  - `65` giây -> `"01:05"`
  - `900` giây -> `"15:00"`

---

### 3.2. Integration & Database Tests

#### TC-05: Khôi phục câu hỏi mẫu không mất câu người dùng tự tạo
- **Các bước:**
  1. Người dùng thêm 1 câu hỏi mới (câu hỏi này có `is_sample = 0`).
  2. Bấm nút **"Khôi phục câu hỏi mẫu"**.
- **Kỳ vọng:**
  - Toàn bộ các câu hỏi mẫu (`is_sample = 1`) được nạp lại đầy đủ.
  - Câu hỏi người dùng tự thêm (`is_sample = 0`) vẫn còn nguyên trong CSDL.

#### TC-06: Thao tác DB trên luồng nền (Background Thread)
- **Kỳ vọng:** Mọi truy vấn đọc/ghi SQLite đều chạy thông qua `AppExecutors.getInstance().diskIO()`, không bao giờ block Main UI Thread, không gây ANR (Application Not Responding).

---

### 3.3. Manual / UI Tests

#### TC-07: Xoay màn hình khi đang làm bài thi
- **Các bước:**
  1. Vào bài thi 10 câu, làm đến câu số 4 (đã chọn B, gắn cờ câu 4).
  2. Đồng hồ đang đếm còn `14:20`.
  3. Xoay ngang màn hình thiết bị (hoặc đổi chế độ màn hình).
- **Kỳ vọng:**
  - Màn hình hiển thị tiếp tục ở câu số 4.
  - Đáp án B vẫn được đánh dấu đang chọn, cờ vẫn được bật.
  - Đồng hồ đếm ngược tiếp tục chính xác từ mốc thời gian thực, không bị nhảy ngược về 15:00.

#### TC-08: Tự động nộp bài khi hết giờ
- **Các bước:** Để đồng hồ chạy về `00:00`.
- **Kỳ vọng:**
  - Hiển thị Toast thông báo hết giờ.
  - Tự động dừng đếm giờ, chấm điểm bài thi, lưu vào CSDL và chuyển sang màn hình Kết quả (`ResultActivity`).

---

## 4. Hướng dẫn chạy Test trong Android Studio

1. Mở project trong Android Studio.
2. Điều hướng đến thư mục:
   - Unit tests: `app/src/test/java/com/example/quizapp/`
   - UI tests: `app/src/androidTest/java/com/example/quizapp/`
3. Click chuột phải vào thư mục test hoặc file test cần chạy và chọn **Run 'All Tests'** (hoặc tổ hợp phím `Ctrl + Shift + F10`).

