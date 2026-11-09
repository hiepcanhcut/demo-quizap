# App Architecture

> Tiêu chí **A3 — Kiến trúc & Thiết kế Dữ liệu**
> Ứng dụng: **Quiz App (Trắc nghiệm)** — Android, Java, SQLite thuần, offline 100%.

Tài liệu này trình bày nhóm em đã xây dựng ứng dụng như thế nào và **vì sao** chọn như vậy. Ứng dụng có 4 nhóm chức năng: quản lý ngân hàng câu hỏi (CRUD, tìm kiếm, lọc, khôi phục câu hỏi mẫu), thi/luyện tập (đồng hồ, gắn cờ, bảng câu hỏi nhanh), xem kết quả chi tiết và lịch sử bài thi.

## 1. Layers

Nhóm em dùng kiến trúc **MVC truyền thống của Android** (Activity – Adapter – SQLiteOpenHelper) theo đúng yêu cầu đề bài, và chia thành 4 lớp. Màn hình (Activity) chỉ hiển thị và nhận sự kiện, không viết SQL. Logic chấm điểm nằm ở lớp Domain, còn truy cập dữ liệu nằm ở lớp Data. Quy tắc: **lớp trên gọi lớp dưới, lớp dưới không gọi ngược lên**.

```mermaid
graph TD
    subgraph Presentation["Presentation (View + Controller)"]
        MAIN["MainActivity"]
        QL["QuestionListActivity<br/>+ QuestionAdapter"]
        QE["QuestionEditActivity"]
        ES["ExamSetupActivity"]
        EX["ExamActivity<br/>+ QuestionGridDialog"]
        RS["ResultActivity<br/>+ ResultDetailAdapter"]
        HI["HistoryActivity<br/>+ HistoryAdapter"]
    end

    subgraph Domain["Domain (Model + Business logic)"]
        MODEL["Model: Question, ExamHistory,<br/>ExamSession, AnswerResult"]
        ENGINE["ExamEngine<br/>dựng đề, chấm điểm"]
        GRADER["ScoreGrader<br/>xếp loại"]
        VALID["QuestionValidator<br/>kiểm tra form nhập"]
    end

    subgraph Data["Data (Data access)"]
        QR["QuestionRepository"]
        HR["HistoryRepository"]
        HELPER["QuizDatabaseHelper<br/>extends SQLiteOpenHelper"]
        SEED["SampleDataProvider<br/>câu hỏi mẫu"]
        QR --> HELPER
        HR --> HELPER
        HELPER --> SEED
    end

    subgraph Storage["Storage (trên thiết bị)"]
        DB[("SQLite: quiz.db")]
        PREF[("SharedPreferences")]
    end

    QL --> QR
    QE --> VALID
    QE --> QR
    ES --> QR
    EX --> ENGINE
    RS --> ENGINE
    RS --> GRADER
    RS --> HR
    HI --> HR
    ENGINE --> MODEL
    QR --> MODEL
    HR --> MODEL
    HELPER --> DB
    HELPER -.-> PREF
```



Điều hướng giữa các màn hình:

```mermaid
flowchart LR
    MAIN["MainActivity"] --> QL["Ngân hàng câu hỏi"]
    MAIN --> ES["Thiết lập bài thi"]
    MAIN --> HI["Lịch sử"]
    QL <--> QE["Thêm / Sửa câu hỏi"]
    ES --> EX["Làm bài"]
    EX --> RS["Kết quả"]
    RS -->|Làm lại| ES
    RS -->|Về chính| MAIN
```


## 2. State Management

**Lựa chọn của nhóm:** không dùng thư viện quản lý state. Nhóm em dùng **biến trạng thái trong Activity + đối tượng `ExamSession` + `onSaveInstanceState` (Bundle)**, còn dữ liệu lâu dài thì để trong **SQLite**.

**Vì sao:**

- Đề bài yêu cầu Java và Android truyền thống (MVC), nên không dùng ViewModel/Compose/Riverpod.
- App chỉ có một chỗ state phức tạp là **phiên thi**, gom hết vào một lớp `ExamSession` nên dễ quản lý.
- Mỗi loại dữ liệu chỉ có **một nơi lưu duy nhất**, tránh việc hai nơi cùng giữ một dữ liệu rồi lệch nhau.
- Nhóm em đã cân nhắc MVVM + LiveData nhưng loại vì thêm nhiều khái niệm so với quy mô app. Nếu mở rộng thì chuyển được vì đã có lớp Repository.

Nhóm em chia state thành 3 loại theo **thời gian hoạt động**:

```mermaid
graph LR
    subgraph P["Bền vững — hoạt động lâu dài, còn sau khi tắt app"]
        P1["Ngân hàng câu hỏi"]
        P2["Lịch sử bài thi"]
    end
    subgraph S["Phiên thi — hoạt động trong 1 lần thi"]
        S1["ExamSession:<br/>questionIds, selected, flagged,<br/>currentIndex, startElapsedMs"]
    end
    subgraph U["UI — hoạt động trong vòng đời Activity"]
        U1["Từ khóa tìm kiếm"]
        U2["Môn đang lọc"]
        U3["Loading / Empty / Content"]
    end
    P --> DB[("SQLite")]
    S --> BUNDLE["Bundle<br/>onSaveInstanceState"]
    U --> VAR["Biến trong Activity"]
```

**Trạng thái màn hình danh sách câu hỏi** (mô hình hóa giống `UiState`):

```text
ListState = Loading | Content(danh sách câu hỏi) | Empty(không có dữ liệu / không khớp bộ lọc)
```

```mermaid
stateDiagram-v2
    [*] --> Loading
    Loading --> Content: có dữ liệu
    Loading --> Empty: rỗng
    Content --> Loading: đổi từ khóa hoặc bộ lọc
    Empty --> Loading: đổi bộ lọc / Khôi phục mẫu
    Content --> Empty: xóa câu cuối cùng
```

**Trạng thái của một lần thi** (máy trạng thái):

```mermaid
stateDiagram-v2
    [*] --> SETUP
    SETUP --> IN_PROGRESS: Bắt đầu, có ít nhất 1 câu
    IN_PROGRESS --> REVIEWING: mở bảng câu hỏi nhanh
    REVIEWING --> IN_PROGRESS: nhảy tới câu bất kỳ
    IN_PROGRESS --> CONFIRM_SUBMIT: bấm Nộp bài
    CONFIRM_SUBMIT --> IN_PROGRESS: Hủy, quay lại làm tiếp
    CONFIRM_SUBMIT --> SUBMITTED: Xác nhận, cảnh báo nếu còn câu trống
    SUBMITTED --> RESULT: chấm điểm, lưu lịch sử
    RESULT --> SETUP: Làm lại đề
    RESULT --> [*]: Về màn hình chính
```

**Xử lý khi xoay màn hình hoặc bị hệ thống thu hồi:**

```mermaid
flowchart TD
    A["Xoay màn hình / app bị đưa xuống nền"] --> B["onSaveInstanceState:<br/>lưu ExamSession vào Bundle"]
    B --> C["Activity được tạo lại: onCreate"]
    C --> D{"savedInstanceState có dữ liệu?"}
    D -->|có| E["Khôi phục ExamSession<br/>nạp lại câu hỏi từ SQLite theo ID"]
    D -->|không| F["Tạo ExamSession mới từ Intent"]
    E --> G["Đồng hồ tính lại từ mốc<br/>SystemClock.elapsedRealtime"]
    F --> G
```

Đồng hồ không cộng dồn từng giây mà lưu **mốc bắt đầu**, mỗi giây lấy `hiện tại − mốc`. Nhờ vậy xoay màn hình hoặc UI bị đứng một chút thì đồng hồ không bị lệch.

---

## 3. Data Flow

Nguyên tắc chung: **sự kiện đi lên (View → Activity → Repository → SQLite), dữ liệu đi xuống (Cursor → Model → Adapter → View)**. Các thao tác DB chạy trên một `ExecutorService` đơn luồng, kết quả đưa về main thread bằng `Handler` để không làm giật giao diện.

### 3.1. Mô hình dữ liệu (ERD)

```mermaid
erDiagram
    QUESTIONS {
        INTEGER id PK
        TEXT content
        TEXT category "có INDEX"
        TEXT option_a
        TEXT option_b
        TEXT option_c
        TEXT option_d
        INTEGER correct_answer "CHECK 0 đến 3"
        TEXT explanation
        INTEGER is_sample "1 là câu hỏi mẫu"
        INTEGER created_at
        INTEGER updated_at
    }
    EXAM_HISTORY {
        INTEGER id PK
        TEXT category
        INTEGER total_questions
        INTEGER correct_count
        REAL score_percent
        INTEGER duration_seconds
        INTEGER taken_at "có INDEX"
    }
```

Hai bảng **cố ý không có khóa ngoại**. `exam_history` là bản ghi tổng hợp tại thời điểm thi, nên sửa hay xóa câu hỏi sau này không làm sai hoặc mất lịch sử.

### 3.2. Luồng chính: Thêm câu hỏi mới

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant View as QuestionEditActivity
    participant Val as QuestionValidator
    participant Repo as QuestionRepository
    participant DB as SQLite
    participant List as QuestionListActivity
    User->>View: nhập nội dung, 4 đáp án, chọn đáp án đúng, giải thích
    User->>View: bấm Lưu
    View->>Val: validate(question)
    alt thiếu dữ liệu
        Val-->>View: lỗi từng ô
        View-->>User: hiển thị lỗi ở ô nhập
    else hợp lệ
        View->>Repo: insert(question) trên luồng nền
        Repo->>DB: INSERT INTO questions
        DB-->>Repo: rowId
        Repo-->>View: thành công
        View-->>List: setResult OK, đóng màn hình
        List->>Repo: search(keyword, category)
        Repo-->>List: danh sách mới
    end
```

### 3.3. Sửa, xóa, tìm kiếm, lọc

```mermaid
flowchart TD
    subgraph Edit["Sửa"]
        E1["Chọn Sửa trên thẻ câu hỏi"] --> E2["Gửi questionId qua Intent"]
        E2 --> E3["getById và điền sẵn form"]
        E3 --> E4["update, cập nhật updated_at"]
    end
    subgraph Del["Xóa"]
        D1["Bấm Xóa"] --> D2{"Hộp thoại cảnh báo:<br/>chắc chắn xóa?"}
        D2 -->|Hủy| D3["Không làm gì"]
        D2 -->|Xóa| D4["delete(id), bỏ thẻ khỏi Adapter"]
    end
    subgraph Find["Tìm kiếm + Lọc"]
        F1["Gõ từ khóa hoặc chọn môn"] --> F2["Debounce 300 ms"]
        F2 --> F3["search(keyword, category):<br/>LIKE trên nội dung, đáp án, giải thích<br/>AND category = ?"]
        F3 --> F4["Cập nhật Adapter"]
    end
```

### 3.4. Luồng làm bài thi và kết quả

```mermaid
sequenceDiagram
    autonumber
    actor User as Người dùng
    participant Setup as ExamSetupActivity
    participant Exam as ExamActivity
    participant Repo as QuestionRepository
    participant Eng as ExamEngine
    participant Hist as HistoryRepository
    participant Res as ResultActivity
    User->>Setup: chọn môn và số câu 5/10/20/Tất cả
    Setup->>Repo: pickRandom(category, n)
    Repo-->>Setup: danh sách questionIds
    Setup->>Exam: Intent chứa questionIds
    Exam->>Repo: getByIds(questionIds)
    Repo-->>Exam: danh sách Question
    loop trong lúc làm bài
        User->>Exam: chọn đáp án, gắn cờ, mở bảng câu hỏi nhanh
        Exam->>Exam: cập nhật selected[], flagged[]
    end
    User->>Exam: bấm Nộp bài
    Exam-->>User: cảnh báo nếu còn câu chưa chọn
    User->>Exam: xác nhận nộp
    Exam->>Eng: grade(questions, selected)
    Eng-->>Exam: số câu đúng, %, danh sách AnswerResult
    Exam->>Hist: insert(ExamHistory) trong transaction
    Exam->>Res: Intent chứa kết quả
    Res-->>User: điểm, %, xếp loại, đối chiếu xanh/đỏ kèm giải thích
```

Quy tắc xếp loại do `ScoreGrader` xử lý:

```mermaid
flowchart LR
    S["Tỷ lệ đúng %"] --> Q1{"≥ 90"}
    Q1 -->|có| R1["Xuất sắc"]
    Q1 -->|không| Q2{"≥ 70"}
    Q2 -->|có| R2["Khá tốt"]
    Q2 -->|không| Q3{"≥ 50"}
    Q3 -->|có| R3["Đạt yêu cầu"]
    Q3 -->|không| R4["Cần cố gắng"]
```

---

## 4. Offline Strategy

App **hoạt động 100% không cần mạng**: không khai báo quyền `INTERNET`, không có server, không đồng bộ. SQLite trên máy là nguồn dữ liệu duy nhất. Vì vậy không có chuyện xung đột dữ liệu giữa nhiều nguồn.

Vì chỉ có một nguồn dữ liệu, chính sách đọc/ghi rất đơn giản:

```mermaid
flowchart LR
    subgraph Read["Đọc"]
        R1["Màn hình cần dữ liệu"] --> R2["Repository truy vấn SQLite"] --> R3["Hiển thị luôn, không cần chờ mạng"]
    end
    subgraph Write["Ghi"]
        W1["Người dùng thêm / sửa / xóa / nộp bài"] --> W2["Ghi vào SQLite<br/>trong transaction"] --> W3["Thành công: cập nhật UI<br/>Lỗi: rollback + thông báo"]
    end
```

**Lần cài đặt đầu tiên: tự nạp câu hỏi mẫu**

```mermaid
flowchart TD
    A["Mở app lần đầu"] --> B["getWritableDatabase"]
    B --> C["onCreate: tạo bảng + index"]
    C --> D["SampleDataProvider.seed"]
    D --> E["Bắt đầu transaction"]
    E --> F["Chèn câu hỏi mẫu<br/>Toán, Lịch sử, Khoa học, Công nghệ<br/>is_sample = 1"]
    F --> G["Kết thúc transaction"]
    G --> H["Danh sách câu hỏi đã có dữ liệu"]
```

**Khôi phục câu hỏi mẫu** (không làm mất câu hỏi do người dùng tự thêm):

```mermaid
flowchart TD
    A["Bấm Khôi phục câu hỏi mẫu"] --> B{"Hộp thoại xác nhận"}
    B -->|Hủy| X["Không đổi gì"]
    B -->|Đồng ý| C["BEGIN transaction"]
    C --> D["DELETE FROM questions WHERE is_sample = 1"]
    D --> E["Chèn lại bộ câu hỏi mẫu"]
    E --> F["COMMIT"]
    F --> G["Nạp lại danh sách"]
    D -.->|lỗi| RB["ROLLBACK, giữ nguyên dữ liệu cũ"]
    E -.->|lỗi| RB
```

**Nâng cấp phiên bản CSDL:** khi tăng `DATABASE_VERSION`, `onUpgrade()` dùng `ALTER TABLE` từng bước, không `DROP TABLE` dữ liệu người dùng.

**Bảo vệ dữ liệu:**

- File `quiz.db` nằm trong bộ nhớ riêng của app, app khác không đọc được.
- Mọi câu truy vấn dùng tham số `?`, không nối chuỗi, nên chống SQL injection.
- App không lưu dữ liệu nhạy cảm như mật khẩu hay thông tin cá nhân.

## 5. Project Structure

Nhóm em tổ chức theo **từng lớp (package-by-layer)** để khớp với sơ đồ ở mục 1.

```text
app/
├── src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/example/quizapp/
│   │   ├── ui/                         ← Presentation
│   │   │   ├── MainActivity.java
│   │   │   ├── question/
│   │   │   │   ├── QuestionListActivity.java
│   │   │   │   ├── QuestionEditActivity.java
│   │   │   │   └── QuestionAdapter.java
│   │   │   ├── exam/
│   │   │   │   ├── ExamSetupActivity.java
│   │   │   │   ├── ExamActivity.java
│   │   │   │   ├── QuestionGridDialog.java
│   │   │   │   ├── ResultActivity.java
│   │   │   │   └── ResultDetailAdapter.java
│   │   │   └── history/
│   │   │       ├── HistoryActivity.java
│   │   │       └── HistoryAdapter.java
│   │   ├── model/                      ← Domain (đối tượng)
│   │   │   ├── Question.java
│   │   │   ├── ExamHistory.java
│   │   │   ├── ExamSession.java
│   │   │   └── AnswerResult.java
│   │   ├── logic/                      ← Domain (nghiệp vụ)
│   │   │   ├── ExamEngine.java
│   │   │   ├── ScoreGrader.java
│   │   │   └── QuestionValidator.java
│   │   ├── data/                       ← Data
│   │   │   ├── QuizDatabaseHelper.java
│   │   │   ├── DbContract.java         ← tên bảng, tên cột
│   │   │   ├── SampleDataProvider.java
│   │   │   ├── QuestionRepository.java
│   │   │   └── HistoryRepository.java
│   │   └── util/
│   │       ├── AppExecutors.java       ← luồng nền + Handler
│   │       ├── TimeFormatter.java      ← định dạng MM:SS
│   │       └── Constants.java
│   └── res/
│       ├── layout/    activity_*.xml, item_*.xml, dialog_*.xml
│       ├── values/    strings.xml, colors.xml, themes.xml
│       ├── drawable/  nền đáp án đúng / sai / đang chọn
│       └── menu/
└── build.gradle   (Java 11, Material Components, RecyclerView, CardView)
```

Quy ước: Activity/Adapter viết PascalCase, layout dùng `activity_*`/`item_*`/`dialog_*`, ID view có tiền tố loại view (`tvTimer`, `btnSubmit`, `rvQuestions`), bảng và cột SQL dùng snake_case.

---

## 6. Design Decisions

Mỗi quyết định bên dưới gồm **phương án nhóm em chọn** (xanh) và **phương án đã cân nhắc nhưng loại** (xám).

```mermaid
flowchart LR
    D1{{"Kiến trúc"}} ==> C1["MVC: Activity + Adapter + SQLiteOpenHelper<br/>Đúng đề, dễ giải thích"]
    D1 -.-> X1["MVVM + Room<br/>Thêm nhiều khái niệm, vượt quy mô app"]

    D2{{"Lưu trữ"}} ==> C2["SQLite thuần<br/>Tự thiết kế bảng, index, không phụ thuộc thư viện"]
    D2 -.-> X2["Room hoặc file JSON<br/>Room ẩn SQL, JSON khó lọc và tìm kiếm"]

    D3{{"Truy cập dữ liệu"}} ==> C3["Thêm lớp Repository<br/>Gom SQL một chỗ, Activity gọn"]
    D3 -.-> X3["Activity gọi thẳng DatabaseHelper<br/>Code lẫn lộn UI và SQL"]

    D4{{"Đáp án đúng"}} ==> C4["Số 0 đến 3 + CHECK<br/>So sánh nhanh với đáp án đã chọn"]
    D4 -.-> X4["Lưu chữ A/B/C/D<br/>Phải đổi qua lại khi chấm"]

    D5{{"Lịch sử thi"}} ==> C5["Chỉ lưu tổng hợp<br/>Đủ yêu cầu, nhẹ, độc lập với câu hỏi"]
    D5 -.-> X5["Lưu từng câu trả lời<br/>Nặng, phụ thuộc câu hỏi còn tồn tại"]

    D6{{"State phiên thi"}} ==> C6["Lưu ID + mảng trạng thái vào Bundle<br/>Nhẹ, dữ liệu luôn lấy lại từ DB"]
    D6 -.-> X6["Truyền cả danh sách Question<br/>Bundle nặng, dễ lỗi TransactionTooLarge"]

    D7{{"Đồng hồ"}} ==> C7["Mốc elapsedRealtime<br/>Chính xác, không lệch khi xoay màn hình"]
    D7 -.-> X7["CountDownTimer cộng dồn<br/>Dễ lệch khi UI chậm"]

    D8{{"Đa luồng"}} ==> C8["ExecutorService + Handler<br/>Thuần Java, ghi DB tuần tự"]
    D8 -.-> X8["AsyncTask<br/>Đã lỗi thời"]

    D9{{"Câu hỏi mẫu"}} ==> C9["Cột is_sample<br/>Khôi phục mẫu mà không mất câu của người dùng"]
    D9 -.-> X9["Xóa toàn bộ DB rồi nạp lại<br/>Mất dữ liệu người dùng"]

    D10{{"Thao tác nguy hiểm"}} ==> C10["Hộp thoại xác nhận<br/>Xóa, nộp thiếu câu, thoát thi, khôi phục mẫu"]
    D10 -.-> X10["Không xác nhận<br/>Dễ mất dữ liệu do bấm nhầm"]

    classDef chosen fill:#E2EFDA,stroke:#548235,color:#1b3a0a
    classDef rejected fill:#EDEDED,stroke:#999,color:#555
    classDef decision fill:#DEEAF6,stroke:#2E75B6,color:#1F4E79
    class C1,C2,C3,C4,C5,C6,C7,C8,C9,C10 chosen
    class X1,X2,X3,X4,X5,X6,X7,X8,X9,X10 rejected
    class D1,D2,D3,D4,D5,D6,D7,D8,D9,D10 decision
```

**Rủi ro chính và cách nhóm em giảm thiểu:**

```mermaid
flowchart LR
    R1["Query DB trên main thread<br/>gây giật UI"] --> M1["Mọi truy vấn qua ExecutorService"]
    R2["Mất phiên thi khi xoay màn hình"] --> M2["Lưu ExamSession vào Bundle"]
    R3["Ngân hàng ít hơn số câu yêu cầu"] --> M3["Giới hạn theo số câu hiện có<br/>và gợi ý Khôi phục mẫu"]
    R4["Nâng cấp schema mất dữ liệu"] --> M4["Migration từng bước<br/>không DROP dữ liệu người dùng"]
    R5["SQL injection ở ô tìm kiếm"] --> M5["Luôn dùng tham số ?"]
```

**Hướng mở rộng nếu có thêm thời gian:** chuyển sang MVVM (vẫn giữ nguyên lớp Repository), đổi sang Room, thêm bảng lưu chi tiết từng câu của bài thi cũ, và xuất/nhập câu hỏi bằng JSON hoặc CSV.
