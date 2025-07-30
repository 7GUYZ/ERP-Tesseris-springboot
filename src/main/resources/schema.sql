-- Qna 테이블 생성 (이미 존재하는 경우 무시)
CREATE TABLE IF NOT EXISTS qna (
    qna_index INT AUTO_INCREMENT PRIMARY KEY,
    question_user_index INT NOT NULL,
    question_title VARCHAR(150) NOT NULL,
    question_desc TEXT NOT NULL,
    answer_user_index INT,
    answer_title VARCHAR(150),
    answer_desc TEXT,
    qna_create_time DATETIME NOT NULL,
    answer_create_time DATETIME,
    FOREIGN KEY (question_user_index) REFERENCES user_tesseris(user_index),
    FOREIGN KEY (answer_user_index) REFERENCES user_tesseris(user_index)
); 