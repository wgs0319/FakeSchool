-- 게시판을 사용하기 위한 사용자 정보들
CREATE TABLE FPROFESSOR (
	professorId				varchar2(50)	PRIMARY KEY,		-- 교수 아이디
	professorname 			varchar2(50)	NOT NULL,			-- 교수 이름
	professorpassword 		varchar2(50)	NOT NULL,			-- 교수 암호
	professordepartment		varchar2(50)	NOT NULL,			-- 교수 학과
	professormail 			varchar2(50)	NOT NULL			-- 교수 이메일주소
);

CREATE TABLE FSTUDENTS (
	studentId				varchar2(50)	PRIMARY KEY,		-- 학번
	studentname 			varchar2(50)	NOT NULL,			-- 학생 이름
	studentpassword 		varchar2(50)	NOT NULL,			-- 학생 암호
	studentdepartment		varchar2(50)	NOT NULL,			-- 학생 학과
	studentmail				varchar2(50)	NOT NULL			-- 학생 이메일주소
);

CREATE TABLE FCOURSE (
	courseId		varchar2(50)	PRIMARY KEY,		-- 과목코드
	coursename 		varchar2(50)	NOT NULL,			-- 과목 이름
	coursecradit	NUMBER			NOT NULL			-- 학점
);

CREATE TABLE FENROLLMENT (
	studentId			varchar2(50)	NOT null,		-- 학번
	courseId			varchar2(50)	NOT null,	 	-- 과목코드
	enrollmentsemester	varchar2(50)	NOT NULL,	 	-- 수강 학기
	enrollmentgrade		varchar2(50)	NOT NULL,	 	-- 수강 성적
	enrollmentscore		NUMBER 			NOT NULL,	 	-- 수강 점수
	enrollmentpoint		NUMBER(5,2)		NOT NULL,		-- 수강 학점
	
	CONSTRAINT pk_fenrollment PRIMARY KEY (studentId, courseId, enrollmentSemester),
	
	CONSTRAINT fk_studentId FOREIGN KEY (studentId) REFERENCES FSTUDENTS (studentId),
	CONSTRAINT fk_courseID FOREIGN KEY (courseId) REFERENCES FCOURSE (courseId)
);