package fakeschooltotal;

import java.sql.*;
import java.util.*;

public class FakeSchool {
	// 사용자로부터 메뉴 혹은 게시물 정보들을 입력받기 위한 용도
	 Scanner sc = new Scanner(System.in);
	 
	// DB 커넥션 접속 객체 변수
	private Connection conn = null;
	
	// 로그인한 교수 아이디 학생 학번
	private String ploginId = null;
	
	// 로그인한 학생 학번
	private String sloginId = null;
		
	public FakeSchool () {
		try {
			// JDBC Driver 등록
			Class.forName("oracle.jdbc.OracleDriver");
			
			// 연결하기
			conn = DriverManager.getConnection(
				"jdbc:oracle:thin:@localhost:1521/orcl",	// oracle 접속정보
				"wgs96",		// oracle 본인계정 이름
				"1234"			// oracle 본인계정 암호
			);
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
	}
	
	// 수강 점수를 등급으로 바꿔주는 메소드
	public String enrollmentGradeCalculator (int enrollmentscore) throws CalculateException {
		if (enrollmentscore < 0 || enrollmentscore > 100) {
			throw new CalculateException ("점수는 0 ~ 100 사이의 값이어야 합니다.");
		}
		
		if (enrollmentscore >= 95) {
			return "A+";
		} else if (enrollmentscore >= 90) {
			return "A";
		} else if (enrollmentscore >= 85) {
			return "B+";
		} else if (enrollmentscore >= 80) {
			return "B";
		} else if (enrollmentscore >= 75) {
			return "C+";
		} else if (enrollmentscore >= 70) {
			return "C";
		} else if (enrollmentscore >= 65) {
			return "D+";
		} else if (enrollmentscore >= 60) {
			return "D";
		} else {
			return "F";
		}
			
	}
	
	// 성적 등급 점수로 환산하는 메소드
	public double enrollmentPointCalculator (String enrollmentGrade) {
		if (enrollmentGrade.equals("A+")) {
			return 4.5;
		} else if (enrollmentGrade.equals("A")) {
			return 4.0;
		} else if (enrollmentGrade.equals("B+")) {
			return 3.5;
		} else if (enrollmentGrade.equals("B")) {
			return 3.0;
		} else if (enrollmentGrade.equals("C+")) {
			return 2.5;
		} else if (enrollmentGrade.equals("C")) {
			return 2.0;
		} else if (enrollmentGrade.equals("D+")) {
			return 1.5;
		} else if (enrollmentGrade.equals("D")) {
			return 1.0;
		} else {
			return 0.0;
		}		
		
	}
	
	// 특정 학생의 평균 점수 계산을 위한 학생과 과목 정보 저장
	class EnrollmentStudentInfo {
		String studentId;
		String courseId;
		int coursecradit;
		String enrollmentSemester;
		double enrollmentPoint;
		
		public EnrollmentStudentInfo (String studentId, String courseId, int coursecradit, double enrollmentPoint, String enrollmentSemester) {
			this.studentId = studentId;
			this.courseId = courseId;
			this.coursecradit = coursecradit;
			this.enrollmentPoint = enrollmentPoint;
			this.enrollmentSemester = enrollmentSemester;
		}
		
		public EnrollmentStudentInfo (String studentId, String enrollmentSemester, int coursecradit, double enrollmentPoint) {
			this.studentId = studentId;
			this.coursecradit = coursecradit;
			this.enrollmentPoint = enrollmentPoint;
			this.enrollmentSemester = enrollmentSemester;
		}
	}
	
	List<EnrollmentStudentInfo> enrollmentStudentInfos = new ArrayList<>();
	
	// 특정 학생의 평균 점수 구하는 메소드
	public double enrollmentAVGCalculator () {
		double totalPoint = 0.0;
		int totalCradit = 0;
		for (EnrollmentStudentInfo enrollmentInfo : enrollmentStudentInfos) {
			double multiplyPoint = enrollmentInfo.enrollmentPoint * enrollmentInfo.coursecradit;
			totalPoint += multiplyPoint;
			totalCradit += enrollmentInfo.coursecradit;
		}
		double avg =  totalPoint / totalCradit;
		return Math.round(avg * 100.0) / 100.0;
	}
	
	// 특정 과목의 평균 점수 계산을 위한 학생과 과목 정보 저장
	class EnrollmentCourseInfo {
		String studentId;
		String courseId;
		int enrollmentScore;
		double enrollmentPoint;
		
		public EnrollmentCourseInfo (String studentId, String courseId, int enrollmentScore) {
			this.studentId = studentId;
			this.courseId = courseId;
			this.enrollmentScore = enrollmentScore;
		}
		
		public EnrollmentCourseInfo (String studentId, String courseId, double enrollmentPoint) {
			this.studentId = studentId;
			this.courseId = courseId;
			this.enrollmentPoint = enrollmentPoint;
		}
	}
	
	List<EnrollmentCourseInfo> enrollmentCourseInfos = new ArrayList<>();
	
	// 특정 과목의 평균 점수 구하는 메소드
		public double courseAVGCalculator () {
			int totalScore = 0;
			int count = 0;
			for (EnrollmentCourseInfo enrollmentInfo : enrollmentCourseInfos) {
				totalScore += enrollmentInfo.enrollmentScore;
				count++;
			}
			if (count == 0) {
				return 0.0;
			}
			
			double avg = (double) totalScore / count;
			return Math.round(avg * 100.0) / 100.0;
		}
	
	// 계산기 오류 처리 메소드
	class CalculateException extends Exception {
		public CalculateException(String message) {
			super(message);
		}
	}
	
	public void mainMenu() {
		while (true) {
			if (ploginId == null && sloginId == null) {
				System.out.println("[FakeSchool 온라인 서비스]");
			} else if (ploginId != null) {
				System.out.println("[FakeSchool 온라인 서비스] 교수: " + ploginId);
			} else {
				System.out.println("[FakeSchool 온라인 서비스] 학번: " + sloginId);
			}
			
			System.out.println("----------------------------------------------------------------------------");
			if (ploginId == null && sloginId == null) {
				System.out.println("| 1.로그인 | 2.전체 과목 열람 | 3.이용 종료 |");
			} else if (ploginId != null){
				System.out.println("| 1.로그아웃 | 2.과목 업무 | 3.성적 업무 | 4.개인정보 수정 | 5.학생 관리 | 6.교수 관리 | 7.이용 종료 |");
			} else {
				System.out.println("| 1.로그아웃 | 2.과목 열람 | 3.성적 조회 | 4.개인정보 수정 | 5.이용 종료 | 99.자퇴 신청 |");
			}
			
			System.out.println("----------------------------------------------------------------------------");
			System.out.print("메뉴선택: ");
			System.out.println();
			
			String menuNo = sc.nextLine();
			if (ploginId == null && sloginId == null) {
				switch(menuNo) {
				case "1":
					login();
					break;
				case "2":
					totalCourseSearch();
					break;
				case "3":
					exit();
					break;
				default:
					System.out.println("잘못된 입력입니다. 다시 선택해 주세요.");
				}
			} else if (ploginId != null) {
				switch(menuNo) {
				case "1":
					logout();
					break;
				case "2":
					courseManagement();
					break;
				case "3":
					enrollmentManagement();
					break;
				case "4":
					infomationManagement();
					break;
				case "5":
					studentManagement();
					break;
				case "6":
					proffessorManagement();
					break;
				case "7":
					exit();
					break;
				default:
					System.out.println("잘못된 입력입니다. 다시 선택해 주세요.");
				}
			} else if (sloginId != null) {
				switch(menuNo) {
				case "1":
					logout();
					break;
				case "2":
					courseManagement();
					break;
				case "3":
					searchStudentEnrollment();
					break;
				case "4":
					infomationManagement();
					break;
				case "5":
					exit();
					break;
				case "99":
					dropout();
					break;
				default:
					System.out.println("잘못된 입력입니다. 다시 선택해 주세요.");
				}
			}
			
		}
		
	}

	// 로그인 시작 메뉴
	private void login() {
		System.out.println("[FakeSchool 로그인]");
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("| 1.교수 로그인 | 2.학생 로그인 | 3.취소 |");
		System.out.println("----------------------------------------------------------------------------");
		System.out.print("메뉴선택: ");
		System.out.println();
		
		String menuNo = sc.nextLine();
		switch(menuNo) {
			case "1":
				proffessorLogin();
				break;
			case "2":
				studentLogin();
				break;
			case "3":
				return;
			default :
				System.out.println("잘못된 입력입니다. 다시 선택해 주세요.");
		}
	}

	// 교수 로그인
	private void proffessorLogin() {
		System.out.println("[FakeSchool 교수 로그인]");
		System.out.print("아이디: ");
		String professorId = sc.nextLine();
		System.out.print("비밀번호: ");
		String professorPassword = sc.nextLine();
		
		String sql = "" +
				"SELECT professorId, professorpassword " +
				"FROM FPROFESSOR WHERE professorId = ?";
		try {
			//PreparedStatement 얻기 및 값 지정
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, professorId);
				
			// select문 실행
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String dbPassword = rs.getString("professorpassword");
				if (professorPassword.equals(dbPassword)) {	// 로그인 성공
					ploginId = professorId;
				} else {
					System.out.println("비밀번호가 일치하지 않습니다.");		
				}
			} else {
				System.out.println("해당 아이디가 존재하지 않습니다.");
			}
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
		mainMenu();
	}

	// 학생 로그인
	private void studentLogin() {
		System.out.println("[FakeSchool 학생 로그인]");
		System.out.print("학번: ");
		String studentId = sc.nextLine();
		System.out.print("비밀번호: ");
		String studentPassword = sc.nextLine();
		
		String sql = "" +
				"SELECT studentId, studentpassword " +
				"FROM FSTUDENTS WHERE studentId = ?";
		try {
			//PreparedStatement 얻기 및 값 지정
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, studentId);
				
			// select문 실행
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String dbPassword = rs.getString("studentpassword");
				if (studentPassword.equals(dbPassword)) {	// 로그인 성공
					sloginId = studentId;
				} else {
					System.out.println("비밀번호가 일치하지 않습니다.");
						
				}
			} else {
				System.out.println("해당 학번이 존재하지 않습니다.");				
			}
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
		mainMenu();
	}
	
	private void logout() {
		String logoutTitle = "[FakeSchool 로그아웃]"; 
		if (ploginId != null) {
			System.out.println(logoutTitle + "교수: " + ploginId);
		} else if (sloginId != null) {
			System.out.println(logoutTitle + "학번: " + sloginId);
		}
		
		System.out.println("로그아웃 하시겠습니까?");
		if(printSubMenu().equals("1")) {
			ploginId = null;
			sloginId = null;
		}
		mainMenu();
	}

	private void courseManagement() {
		String CourseSearchTitle = "[FakeSchool 과목 ";
		if (ploginId != null) {
			CourseSearchTitle = CourseSearchTitle + "업무] 교수: " + ploginId;
		} else if (sloginId != null) {
			CourseSearchTitle = CourseSearchTitle + "열람] 학번: " + sloginId;
		} 
		System.out.println(CourseSearchTitle);
		System.out.println("----------------------------------------------------------------------------");
		if (ploginId != null) {
			// 학생 전체 과목 내과목
			System.out.println("| 1.전체 과목 열람 | 2.과목 추가 | 3.과목 수정 | 4.과목 삭제 | 5.취소 |");
		} else if (sloginId != null) {
			System.out.println("| 1.전체 과목 열람 | 2.내 과목 열람 | 3.취소 |");
		}
		System.out.println("----------------------------------------------------------------------------");
		System.out.print("메뉴선택: ");
		System.out.println();
		
		while (true) {
			String menuNo = sc.nextLine();
			if (ploginId != null) {
				switch(menuNo) {
				case "1":
					totalCourseSearch();
					break;
				case "2":
					addCourse();
					break;
				case "3":
					updateCourse();
					break;
				case "4":
					deleteCourse();
					break;
				case "5":
					mainMenu();
					break;
				default :
					System.out.println("잘못된 입력입니다. 다시 선택해 주세요.");
				}
			} else if (sloginId != null) {
				switch(menuNo) {
				case "1":
					totalCourseSearch();
					break;
				case "2":
					studentCourseSearch();
					break;
				case "3":
					mainMenu();
					break;
				default :
					System.out.println("잘못된 입력입니다. 다시 선택해 주세요.");
				}
			}
		}
		
	}

	private void totalCourseSearch() {
		String totalCourseSearchTitle = "[FakeSchool 전체 과목 열람]";
		if (ploginId != null) {
			totalCourseSearchTitle = totalCourseSearchTitle + "교수: " + ploginId;
		} else if (sloginId != null) {
			totalCourseSearchTitle = totalCourseSearchTitle + "학번: " + sloginId;
		}
		System.out.println(totalCourseSearchTitle);
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("| 1.과목 코드 |     2.과목 이름     | 3.학점 |");
		
		// FCOURSE 테이블에서 과목 정보 가져와서 출력
		try {
			String sql = "" +
					"SELECT courseId, coursename, coursecradit " +
					"FROM FCOURSE";
			//PreparedStatement 얻기 및 값 지정
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Course course = new Course();
				course.setCourseId(rs.getString("courseId"));
				course.setCourseName(rs.getString("coursename"));
				course.setCourseCradit(rs.getInt("coursecradit"));
				
				System.out.printf("%-12s%-18s%4d\n",
									course.getCourseId(),
									course.getCourseName(),
									course.getCourseCradit()
									);
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			exit();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
		
		mainMenu();
	}
	
	private void studentCourseSearch() {
		System.out.println("[FakeSchool 내 과목 열람] 학번: " + sloginId);
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("| 1.과목 코드 |     2.과목 이름     | 3.해당 학기 | 4.해당 학점 |");
		
		// FCOURSE 테이블에서 과목 정보 가져와서 출력
		try {
			String sql = "" +
					"SELECT studentId, courseId, enrollmentsemester " +
					"FROM FENROLLMENT WHERE studentId = ?";
			//PreparedStatement 얻기 및 값 지정
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, sloginId);
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				Enrollment enrollment = new Enrollment();
				enrollment.setStudentId(rs.getString("studentId"));
				enrollment.setCourseId(rs.getString("courseId"));
				enrollment.setEnrollmentSemester(rs.getString("enrollmentsemester"));
				
				String sql2 = "" +
						"SELECT courseId, coursename, coursecradit " +
						"FROM FCOURSE WHERE courseId = ?";
				//PreparedStatement 얻기 및 값 지정
				PreparedStatement pstmt2 = conn.prepareStatement(sql2);
				pstmt2.setString(1, enrollment.getCourseId());
				ResultSet rs2 = pstmt2.executeQuery();
				
				if (rs2.next()) {
					Course course = new Course();
					course.setCourseId(rs2.getString("courseId"));
					course.setCourseName(rs2.getString("coursename"));
					course.setCourseCradit(rs2.getInt("coursecradit"));
					
					System.out.printf("%-12s%-18s%-15s%4d\n",
							enrollment.getCourseId(),
							course.getCourseName(),
							enrollment.getEnrollmentSemester(),
							course.getCourseCradit()
							);
					
				}
				rs2.close();
				pstmt2.close();
			}
			
			rs.close();
			pstmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			exit();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
		
		mainMenu();
	}
	
	private void addCourse() {
		System.out.println("[FakeSchool 과목 추가] 교수: " + ploginId);
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("[추가할 과목 정보 입력]");
		System.out.print("과목 코드: ");
		String courseId = sc.nextLine();
		System.out.print("과목 이름: ");
		String courseName = sc.nextLine();
		System.out.print("학점: ");
		String courseCradit = sc.nextLine();
		System.out.println("해당 과목을 추가하시겠습니까?");
		
		if (printSubMenu().equals("1")) {
			try {
				// 과목 등록하는 sql
				String sql = "" +
						"INSERT INTO FCOURSE (courseId, coursename, coursecradit) " +
						"VALUES (?, ?, ?)";
				//PreparedStatement 얻기 및 값 지정
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, courseId);		 // varchar2
				pstmt.setString(2, courseName);    // varchar2
				pstmt.setInt(3, Integer.parseInt(courseCradit));		 // varchar2

				//SQL문 실행
				int rows = pstmt.executeUpdate();
				
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				exit();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
		}
		courseManagement();
	}

	private void updateCourse() {
		System.out.println("[FakeSchool 과목 수정] 교수: " + ploginId);
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("[수정할 과목 코드 입력]");
		System.out.print("과목 코드: ");
		String courseId = sc.nextLine();
		
		// 기존 정보를 저장할 변수
	    String currentCourseName = null;
	    String currentCourseCradit = null;
		
	    try {
			String sql = "" +
					"SELECT coursename, coursecradit " +
					"FROM FCOURSE WHERE courseId = ?";
		
			PreparedStatement pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, courseId);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            currentCourseName = rs.getString("coursename");
	            currentCourseCradit = rs.getString("coursecradit");
	        }
	        rs.close();
	        pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
	    
	    System.out.println("이 과목을 수정 하시겠습니까?");
		if (printSubMenu().equals("1")) {
			try {
				// 과목 조회하는 sql
				String sql = "" +
						"SELECT courseId " +
						"FROM FCOURSE WHERE courseId = ?";
				
				//PreparedStatement 얻기 및 값 지정
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, courseId);		 // varchar2
				
				// select문 실행
				ResultSet rs = pstmt.executeQuery();
				
				if (rs.next()) {
					System.out.println("[FakeSchool 과목 수정 내용 입력](변경하지 않으려면 엔터를 누르세요)");
					System.out.print("과목 이름: ");
					String input = sc.nextLine().trim();
					String courseName = input.isEmpty() ? currentCourseName : input;
					System.out.print("학점: ");
					input = sc.nextLine().trim();
					String courseCradit = input.isEmpty() ? currentCourseCradit : input;
					
					sql = new StringBuilder()
							.append("UPDATE           ")
							.append("  FCOURSE         ")
							.append("SET              ")
							.append("  coursename = ?, ")	// 1
							.append("  coursecradit = ?  ")	// 2
							.append("WHERE            ")	
							.append("  courseId = ?        ")	// 3
							.toString();
					//PreparedStatement 얻기 및 값 저장
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, courseName);
					pstmt.setInt(2, Integer.parseInt(courseCradit));
					pstmt.setString(3, courseId);
					
					// SQL문 실행
					int rows = pstmt.executeUpdate();
					pstmt.close();
				} else {
					System.out.println("해당 과목번호가 존재하지 않습니다.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				exit();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
		}
		courseManagement();
	}
	
	private void deleteCourse() {
		System.out.println("[FakeSchool 과목 삭제] 교수: " + ploginId);
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("[삭제할 과목 코드 입력]");
		System.out.print("과목 코드: ");
		String courseId = sc.nextLine();
		
		System.out.println("이 과목을 삭제 하시겠습니까?");
		if (printSubMenu().equals("1")) {
			try {
				String sql = "" +
						"SELECT courseId " +
						"FROM FCOURSE WHERE courseId = ?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, courseId);
				
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					sql = "DELETE FROM FCOURSE WHERE courseId = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, courseId);
					
					pstmt.executeUpdate();
					pstmt.close();
				} else {
					System.out.println("해당 과목 코드가 존재하지 않습니다");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				exit();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
		
		}
		courseManagement();
	}
	
	private void enrollmentManagement() {
		System.out.println("[FakeSchool 성적 업무] 교수: " +ploginId);
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("| 1.성적 등록 | 2.성적 조회 | 3.성적 수정 | 4.성적 삭제 | 5.취소 |");
		System.out.println("----------------------------------------------------------------------------");
		System.out.print("메뉴선택: ");
		System.out.println();
		
		String menuNo = sc.nextLine();
		switch(menuNo) {
		case "1":
			addEnrollment();
			break;
		case "2":
			searchEnrollment();
			break;
		case "3":
			updateEnrollment();
			break;
		case "4":
			deleteEnrollment();
			break;
		case "5":
			mainMenu();
			break;
		default:
			System.out.println("잘못된 입력입니다. 다시 선택해 주세요.");
		}
	}

	private void addEnrollment() {
		System.out.println("[FakeSchool 성적 추가]  교수: " + ploginId);
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("[추가할 성적 정보 입력]");
		System.out.print("학생 학번: ");
		String studentId = sc.nextLine();
		System.out.print("과목 코드: ");
		String courseId = sc.nextLine();
		System.out.print("해당 학기: ");
		String enrollmentSemester = sc.nextLine();
		System.out.print("점수: ");
		int enrollmentScore = sc.nextInt();
		sc.nextLine();
		System.out.println("해당 성적을 추가하시겠습니까?");
		
		if (printSubMenu().equals("1")) {
			try {
				String sql = "" +
						"INSERT INTO FENROLLMENT (studentId, courseId, enrollmentSemester, enrollmentGrade, enrollmentScore, enrollmentpoint) " +
						"VALUES (?, ?, ?, ?, ?, ?)";
				//PreparedStatement 얻기 및 값 지정
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, studentId);		 // varchar2
				pstmt.setString(2, courseId);    // varchar2
				pstmt.setString(3, enrollmentSemester);		 // varchar2
				pstmt.setString(4, enrollmentGradeCalculator (enrollmentScore));    // varchar2
				pstmt.setInt(5, enrollmentScore);		 // number
				pstmt.setDouble(6, enrollmentPointCalculator(enrollmentGradeCalculator (enrollmentScore)));

				//SQL문 실행
				int rows = pstmt.executeUpdate();
				
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				exit();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
		}
		enrollmentManagement();
	}

	private void searchEnrollment() {
		System.out.println("[FakeSchool 성적 조회] 교수: " +ploginId);
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("| 1.전체 학생 평균 학점 조회 | 2.특정 학생 성적 조회 | 3.과목별 성적 조회 | 4.취소 |");
		System.out.println("----------------------------------------------------------------------------");
		System.out.print("메뉴선택: ");
		System.out.println();
		
		String menuNo = sc.nextLine();
		switch(menuNo) {
		case "1":
			searchTotalStudentEnrollment();
			break;
		case "2":
			searchStudentEnrollment();
			break;
		case "3":
			searchCourseEnrollment();
			break;
		case "4":
			mainMenu();
			break;
		default:
			System.out.println("잘못된 입력입니다. 다시 선택해 주세요.");
		}
	}

	// 평균 학점을 coursecradit까지 고려해서 계산해야함
	private void searchTotalStudentEnrollment() {
		System.out.println("[FakeSchool 전체 학생 평균 학점 조회] 교수: " + ploginId);
		
		System.out.println("| 1.학생 학번 | 2.학생 이름 |    3.수강 학기    | 4.평균 학점 |");
		
		// fenrollment테이블에서 게시물을 가져와서 출력
		String sql = "" +
				"SELECT DISTINCT studentId, enrollmentsemester " +
				"FROM FENROLLMENT ORDER BY studentId, enrollmentsemester";
		try {
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
				
			double max = Double.NEGATIVE_INFINITY;
			String maxStudentId = null;
			double min = Double.POSITIVE_INFINITY;
	    	String minStudentId = null;
	    	
	    	String studentname = "";
			
			while (rs.next()) {
				Enrollment enrollment = new Enrollment();
				enrollment.setStudentId(rs.getString("studentId"));
				enrollment.setEnrollmentSemester(rs.getString("enrollmentsemester"));
				
				String sql2 = "" +
						"SELECT studentname " +
						"FROM FSTUDENTS WHERE studentId = ?";
				//PreparedStatement 얻기 및 값 지정
				PreparedStatement pstmt2 = conn.prepareStatement(sql2);
				pstmt2.setString(1, rs.getString("studentId"));
				ResultSet rs2 = pstmt2.executeQuery();
				
				if (rs2.next()) {
					Student student = new Student();
					studentname = rs2.getString("studentname");
				}
				pstmt2.close();
				rs2.close();
					
				String sql3 = "" +
						"SELECT E.enrollmentpoint, C.coursecradit " +
						"FROM FENROLLMENT E JOIN FCOURSE C ON E.courseId = C.courseId " +
						"WHERE E.studentId = ? AND E.enrollmentsemester = ?";
				//PreparedStatement 얻기 및 값 지정
				PreparedStatement pstmt3 = conn.prepareStatement(sql3);
				pstmt3.setString(1, rs.getString("studentId"));
				pstmt3.setString(2, rs.getString("enrollmentsemester"));
				ResultSet rs3 = pstmt3.executeQuery();
					
				double totalPoint = 0;
				int totalCradit = 0;
					
				while (rs3.next()) {
					double point = rs3.getDouble("EnrollmentPoint");
					int cradit = rs3.getInt("coursecradit");
					
					totalPoint += point * cradit;
					totalCradit += cradit;
				}	
				rs3.close();
				pstmt3.close();	
				
				double AVG =  totalCradit > 0 ? totalPoint / totalCradit : 0;	
					
				EnrollmentStudentInfo enrollmentInfo = new EnrollmentStudentInfo(
							enrollment.getStudentId(),
							enrollment.getEnrollmentSemester(),
							totalCradit,
							AVG
							);
				enrollmentStudentInfos.add(enrollmentInfo);
					
				System.out.printf("%-12s%-12s%-18s%.2f\n",
				            enrollment.getStudentId(),
				            studentname,
				            enrollment.getEnrollmentSemester(),
				            AVG
						    );
							 
					
						
				if (AVG > max) {
					max = AVG;
					maxStudentId = enrollment.getStudentId();
				}
				if (AVG < min) {
					min = AVG;
					minStudentId = enrollment.getStudentId();
				}
				
				
			}
			rs.close();
			pstmt.close();
			
			double totalAVG = enrollmentAVGCalculator();
			enrollmentStudentInfos.clear();
			System.out.println("평균학점: " + String.format("%.2f", totalAVG));
			System.out.println("최고학점: " + String.format("%.2f", max) + ", 학번: " + maxStudentId);
			System.out.println("최저학점: " + String.format("%.2f", min) + ", 학번: " + minStudentId);
		} catch (SQLException e) {
			e.printStackTrace();
			exit();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
	
		searchEnrollment();
		
	}

	private void searchStudentEnrollment() {
		String searchEnrollmentTitle = "[FakeSchool 학생 성적 조회]";
		if (ploginId != null) {
			searchEnrollmentTitle = searchEnrollmentTitle + " 교수: " + ploginId;
		} else if (sloginId != null) {
			searchEnrollmentTitle = searchEnrollmentTitle + " 학번: " + sloginId;
		}
		System.out.println(searchEnrollmentTitle);
		
		String studentId = null;
		
		if (ploginId != null) {
			System.out.println("[조회할 학번 입력]");
			System.out.print("학번: ");
			studentId = sc.nextLine();
		} else if (sloginId != null) {
			studentId = sloginId;
		}
		
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("| 1.과목 코드 |     2.과목 이름     | 3.해당 학기 | 4.학점 | 5.점수 |");
		
		
		// fenrollment테이블에서 studentId에 맞는 게시물을 가져와서 출력
		String sql = "" +
				"SELECT courseId, enrollmentsemester, enrollmentscore, enrollmentpoint " +
				"FROM FENROLLMENT WHERE studentId = ?";
			
		try {
			// PreparedStatement 얻기 및 값 지정
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, studentId);
				
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Enrollment enrollment = new Enrollment();
				enrollment.setCourseId(rs.getString("courseId"));
				enrollment.setEnrollmentSemester(rs.getString("enrollmentsemester"));
				enrollment.setEnrollmentScore(rs.getInt("enrollmentScore"));
				enrollment.setEnrollmentPoint(rs.getDouble("enrollmentpoint"));
				
				String sql2 = "" +
						"SELECT coursename, coursecradit " +
						"FROM FCOURSE WHERE courseId = ?";
				//PreparedStatement 얻기 및 값 지정
				PreparedStatement pstmt2 = conn.prepareStatement(sql2);
				pstmt2.setString(1, enrollment.getCourseId());
				ResultSet rs2 = pstmt2.executeQuery();
				
				if (rs2.next()) {
					Course course = new Course();
					course.setCourseName(rs2.getString("coursename"));
					course.setCourseCradit(rs2.getInt("coursecradit"));
					
					System.out.printf("%-12s%-18s%-12s%-6s%4d\n",
							enrollment.getCourseId(),
							course.getCourseName(),
							enrollment.getEnrollmentSemester(),
							enrollment.getEnrollmentPoint(),
							enrollment.getEnrollmentScore()
							);
					
					EnrollmentStudentInfo enrollmentInfo = new EnrollmentStudentInfo(
							enrollment.getStudentId(),
							enrollment.getCourseId(),
							course.getCourseCradit(),
							enrollment.getEnrollmentPoint(),
							enrollment.getEnrollmentSemester()
							);
					enrollmentStudentInfos.add(enrollmentInfo);
				}
				rs2.close();
				pstmt2.close();
			}
			rs.close();
			pstmt.close();
			
			double AVG = enrollmentAVGCalculator();
			enrollmentStudentInfos.clear();
			System.out.println("평균학점: " + AVG);
		} catch (SQLException e) {
			e.printStackTrace();
			exit();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
		if (ploginId != null) {
			searchEnrollment();
		} else if (sloginId != null) {
			mainMenu();
		}
	
	}
		
	private void searchCourseEnrollment() {
		System.out.println("[FakeSchool 학생 성적 조회] 교수: " + ploginId);
		
		System.out.println("[조회할 과목 코드 입력]");
		System.out.print("과목 코드: ");
		String courseId = sc.nextLine();
		
		System.out.println("----------------------------------------------------------------------------");
		
		String sql = "" +
				"SELECT courseId, coursename, coursecradit " +
				"FROM FCOURSE WHERE courseId = ?";
		try {
			
			// PreparedStatement 얻기 및 값 지정
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, courseId);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				Course course = new Course();
				course.setCourseId(rs.getString("courseId"));
				course.setCourseName(rs.getString("coursename"));
				course.setCourseCradit(rs.getInt("coursecradit"));
				
				System.out.println("| 1.과목 코드 |     2.과목 이름     | 3.과목 학점 |");
				System.out.println("----------------------------------------------------------------------------");
				
				System.out.printf("%-12s%-18s%4d\n",
						course.getCourseId(),
						course.getCourseName(),
						course.getCourseCradit()
						);
				System.out.println("----------------------------------------------------------------------------");
				System.out.println("| 1.학생 학번 | 2.학생 이름 | 3.수강 학기 | 4.수강 성적 | 5.수강 점수 | 6.학점 |");
				System.out.println("----------------------------------------------------------------------------");
			}
			
			// fenrollment테이블에서 courseId에 맞는 게시물을 가져와서 출력
			String sql2 = "" +
					"SELECT studentId, courseId, enrollmentsemester, enrollmentgrade, enrollmentscore, enrollmentpoint " +
					"FROM FENROLLMENT WHERE courseId = ?";
			
			//PreparedStatement 얻기 및 값 지정
			PreparedStatement pstmt2 = conn.prepareStatement(sql2);
			pstmt2.setString(1, rs.getString("courseId"));
			ResultSet rs2 = pstmt2.executeQuery();
			
			
			while (rs2.next()) {
				Enrollment enrollment = new Enrollment();
				enrollment.setStudentId(rs2.getString("studentId"));
				enrollment.setCourseId(rs2.getString("courseId"));
				enrollment.setEnrollmentSemester(rs2.getString("enrollmentsemester"));
				enrollment.setEnrollmentGrade(rs2.getString("enrollmentgrade"));
				enrollment.setEnrollmentScore(rs2.getInt("enrollmentScore"));
				enrollment.setEnrollmentPoint(rs2.getDouble("enrollmentpoint"));
				
				String sql3 = "" +
						"SELECT studentId, studentname " +
						"FROM FSTUDENTS WHERE studentId = ?";
				//PreparedStatement 얻기 및 값 지정
				PreparedStatement pstmt3 = conn.prepareStatement(sql3);
				pstmt3.setString(1, enrollment.getStudentId());
				ResultSet rs3 = pstmt3.executeQuery();
				
				if (rs3.next()) {
					Student student = new Student();
					student.setStudentId(rs3.getString("studentId"));
					student.setStudentName(rs3.getString("studentname"));
					
					System.out.printf("%-12s%-12s%-12s%-12s%8d%6.2f\n",
							student.getStudentId(),
							student.getStudentName(),
							enrollment.getEnrollmentSemester(),
							enrollment.getEnrollmentGrade(),
							enrollment.getEnrollmentScore(),
							enrollment.getEnrollmentPoint()
							);
					
					EnrollmentCourseInfo enrollmentInfo = new EnrollmentCourseInfo(
							student.getStudentId(),
							enrollment.getCourseId(),
							enrollment.getEnrollmentScore()
							);
					enrollmentCourseInfos.add(enrollmentInfo);
					
				}
				rs3.close();
				pstmt3.close();
				
				
			}
			rs2.close();
			pstmt2.close();	
			
			rs.close();
			pstmt.close();
			
			int max = Integer.MIN_VALUE;
			String maxStudentId = null;
	        int min = Integer.MAX_VALUE;
	        String minStudentId = null;
	        
	        for (EnrollmentCourseInfo enrollmentInfo : enrollmentCourseInfos ) {
	        	if (enrollmentInfo.enrollmentScore < min) {
	        		min = enrollmentInfo.enrollmentScore;
	        		minStudentId = enrollmentInfo.studentId;
	        	}
	        	if (enrollmentInfo.enrollmentScore > max) {
	        		max = enrollmentInfo.enrollmentScore;
	        		maxStudentId = enrollmentInfo.studentId;
	        	}
	        }
			
			double AVG = courseAVGCalculator();
			enrollmentCourseInfos.clear();
			System.out.println("평균점수: " + AVG);
			System.out.println("최저점수: " + min + " 최저점수 획득 학번: " + minStudentId);
			System.out.println("최고점수: " + max + " 최고점수 획득 학번: " + maxStudentId);
			System.out.println("----------------------------------------------------------------------------");
		} catch (SQLException e) {
			e.printStackTrace();
			exit();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
	
		searchEnrollment();
	}
	
	private void updateEnrollment() {
		System.out.println("[FakeSchool 성적 수정]  교수: " + ploginId);
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("[수정할 성적 정보 입력]");
		System.out.print("학생 학번: ");
		String studentId = sc.nextLine();
		System.out.print("과목 코드: ");
		String courseId = sc.nextLine();
		System.out.print("점수: ");
		int enrollmentScore = sc.nextInt();
		sc.nextLine();
		System.out.println("해당 성적을 수정하시겠습니까?");
		
		if (printSubMenu().equals("1")) {
			try {
				String sql2 = "" +
					"SELECT studentId, courseId " +
					"FROM FENROLLMENT WHERE studentId = ? AND courseId = ?";
				PreparedStatement pstmt2 = conn.prepareStatement(sql2);
				pstmt2.setString(1, studentId);		 // varchar2
				pstmt2.setString(2, courseId);    // varchar2
				ResultSet rs = pstmt2.executeQuery();
				if (rs.next()) {
					String sql = new StringBuilder()
							.append("UPDATE                    ")
							.append("	FENROLLMENT            ")
							.append("SET                       ")
							.append("	enrollmentgrade = ?,   ")
							.append("	enrollmentscore = ?,   ")
							.append("	enrollmentpoint = ?    ")
							.append("WHERE                     ")
							.append("	studentId = ? AND      ")
							.append("	courseId = ?           ")
							.toString();
					//PreparedStatement 얻기 및 값 지정
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, enrollmentGradeCalculator (enrollmentScore));		 // varchar2
					pstmt.setInt(2, enrollmentScore);    // number
					pstmt.setDouble(3, enrollmentPointCalculator(enrollmentGradeCalculator (enrollmentScore)));
					pstmt.setString(4, studentId);		 // varchar2
					pstmt.setString(5, courseId);    // varchar2
					
					//SQL문 실행
					int rows = pstmt.executeUpdate();
					rs.close();
					pstmt.close();
				} else {
					System.out.println("해당 성적 정보가 존재하지 않습니다.");
				}
				pstmt2.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
				exit();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
		}
		enrollmentManagement();
	}
		
	private void deleteEnrollment() {
		System.out.println("[FakeSchool 성적 삭제]  교수: " + ploginId);
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("[삭제할 성적 정보 입력]");
		System.out.print("학생 학번: ");
		String studentId = sc.nextLine();
		System.out.print("과목 코드: ");
		String courseId = sc.nextLine();
		System.out.println("해당 성적을 삭제하시겠습니까?");
		
		if (printSubMenu().equals("1")) {
			try {
				String sql2 = "" +
						"SELECT studentId, courseId " +
						"FROM FENROLLMENT WHERE studentId = ? AND courseId = ?";
					PreparedStatement pstmt2 = conn.prepareStatement(sql2);
					pstmt2.setString(1, studentId);		 // varchar2
					pstmt2.setString(2, courseId);    // varchar2
					ResultSet rs = pstmt2.executeQuery();
					if (rs.next()) {
						String sql = "DELETE FROM FENROLLMENT WHERE studentId = ? AND courseId = ?";
						PreparedStatement pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, studentId);
						pstmt.setString(2, courseId);
						pstmt.executeUpdate();
						rs.close();
						pstmt.close();
					}
				pstmt2.close();	
			} catch (SQLException e) {
				e.printStackTrace();
				exit();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
		}
		enrollmentManagement();
	}
	
	private void infomationManagement() {
		String InfomationManagementTitle = "[FakeSchool 개인정보 수정]";
		if (ploginId != null) {
			InfomationManagementTitle = InfomationManagementTitle + " 교수: " + ploginId;
		} else if (sloginId != null) {
			InfomationManagementTitle = InfomationManagementTitle + " 학번: " + sloginId;
		}
		
		// 기존 정보를 저장할 변수
	    String currentProfessorId = null;
	    String currentProfessorName = null;
	    String currentProfessorPassword = null;
	    String currentProfessorDepartment = null;
	    String currentProfessorEmail = null;
	    
	    String currentStudentName = null;
	    String currentStudentPassword = null;
	    String currentStudentEmail = null;
	    
	    // 새 정보를 저장할 변수
		String professorId = null;
		String professorName = null;
		String professorPassword = null;
		String professorDepartment = null;
		String professorEmail = null;
		
		String studentName = null;
		String studentPassword = null;
		String studentEmail = null;
		
		try {
			String sql = null;
			if (ploginId != null) {
				sql = "" +
						"SELECT professorId, professorname, professorpassword,	professordepartment, professormail " +
						"FROM FPROFESSOR WHERE professorId = ?";
			} else if (sloginId != null) {
				sql = "" +
						"SELECT studentname, studentpassword, studentmail " +
						"FROM FSTUDENTS WHERE studentId = ?";
			}
			PreparedStatement pstmt = conn.prepareStatement(sql);
	        if (ploginId != null) {
	            pstmt.setString(1, ploginId);
	        } else if (sloginId != null) {
	            pstmt.setString(1, sloginId);
	        }
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            if (ploginId != null) {
	                currentProfessorId = rs.getString("professorId");
	                currentProfessorName = rs.getString("professorname");
	                currentProfessorPassword = rs.getString("professorpassword");
	                currentProfessorDepartment = rs.getString("professordepartment");
	                currentProfessorEmail = rs.getString("professormail");
	            } else if (sloginId != null) {
	                currentStudentName = rs.getString("studentname");
	                currentStudentPassword = rs.getString("studentpassword");
	                currentStudentEmail = rs.getString("studentmail");
	            }
	        }
	        rs.close();
	        pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
		
		System.out.println(InfomationManagementTitle);
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("[수정할 개인 정보 입력](변경하지 않으려면 엔터를 누르세요)");
		if (ploginId != null) {
			System.out.print("아이디: ");
			String input = sc.nextLine().trim();
	        professorId = input.isEmpty() ? currentProfessorId : input;
		}
		System.out.print("이름: ");
		if (ploginId != null) {
			String input = sc.nextLine().trim();
	        professorName = input.isEmpty() ? currentProfessorName : input;
		} else if (sloginId != null) {
			String input = sc.nextLine().trim();
	        studentName = input.isEmpty() ? currentStudentName : input;
		}
		System.out.print("비밀번호: ");
		if (ploginId != null) {
			String input = sc.nextLine().trim();
	        professorPassword = input.isEmpty() ? currentProfessorPassword : input;
		} else if (sloginId != null) {
			String input = sc.nextLine().trim();
	        studentPassword = input.isEmpty() ? currentStudentPassword : input;
		}
		if (ploginId != null) {
			System.out.print("학과: ");
			String input = sc.nextLine().trim();
	        professorDepartment = input.isEmpty() ? currentProfessorDepartment : input;
		}
		System.out.print("이메일: ");
		if (ploginId != null) {
			String input = sc.nextLine().trim();
	        professorEmail = input.isEmpty() ? currentProfessorEmail : input;
		} else if (sloginId != null) {
			String input = sc.nextLine().trim();
	        studentEmail = input.isEmpty() ? currentStudentEmail : input;
		}
		System.out.println("개인정보를 수정하시겠습니까?");
		if (printSubMenu().equals("1")) {
			try {
				// boards 테이블에서 게시물 정보 수정
				String sql = null;
				if (ploginId != null) {
					sql = new StringBuilder()
							.append("UPDATE         			")
							.append("  FPROFESSOR      			")
							.append("SET              	   	   	")
							.append("  professorId = ?,	 		")	// 1
							.append("  professorname = ?, 	 	")	// 2
							.append("  professorpassword = ?, 	")	// 3
							.append("  professordepartment = ?, ")	// 4
							.append("  professormail = ?  		")	// 5
							.append("WHERE          		    ")	
							.append("  professorId = ?          ")	// 6
							.toString();
				} else if (sloginId != null) {
					sql = new StringBuilder()
							.append("UPDATE         			")
							.append("  FSTUDENTS      			")
							.append("SET              	   	   	")
							.append("  studentname = ?, 	 	")	// 1
							.append("  studentpassword = ?, 	")	// 2
							.append("  studentmail = ?  		")	// 3
							.append("WHERE          		    ")	
							.append(" studentId = ?          	")	// 4
							.toString();
				}
						
				//PreparedStatement 얻기 및 값 저장
				PreparedStatement pstmt = conn.prepareStatement(sql);
				if (ploginId != null) {
					pstmt.setString(1, professorId);
					pstmt.setString(2, professorName);
					pstmt.setString(3, professorPassword);
					pstmt.setString(4, professorPassword);
					pstmt.setString(5, professorEmail);
					pstmt.setString(6, ploginId);
				} else if (sloginId != null) {
					pstmt.setString(1, studentName);
					pstmt.setString(2, studentPassword);
					pstmt.setString(3, studentEmail);
					pstmt.setString(4, sloginId);
				}
				
				// SQL문 실행
				int rows = pstmt.executeUpdate();
				if (ploginId != null) {
					ploginId = professorId;
				}
				pstmt.close();
			} catch(Exception e) {
				e.printStackTrace();
				exit();
			}
		}
		mainMenu();
	}
	
	private void studentManagement() {
		System.out.println("[FakeSchool 학생 관리] 교수: " +ploginId);
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("| 1.학생 정보 등록 | 2.학생 조회 | 3.학생 정보 수정 | 4.학생 정보 삭제 | 5.취소 |");
		System.out.println("----------------------------------------------------------------------------");
		System.out.print("메뉴선택: ");
		System.out.println();
		
		String menuNo = sc.nextLine();
		switch(menuNo) {
		case "1":
			addStudent();
			break;
		case "2":
			searchStudent();
			break;
		case "3":
			updateStudent();
			break;
		case "4":
			deleteStudent();
			break;
		case "5":
			mainMenu();
			break;
		default:
			System.out.println("잘못된 입력입니다. 다시 선택해 주세요.");
		}
	}

	private void addStudent() {
		System.out.println("[FakeSchool 학생 정보 등록] 교수: " +ploginId);
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("[등록할 학생 정보 입력]");
		System.out.print("학생 학번: ");
		String studentId = sc.nextLine();
		System.out.print("학생 이름: ");
		String studentName = sc.nextLine();
		System.out.print("학생 비밀번호: ");
		String studentPassword = sc.nextLine();
		System.out.print("학생 학과: ");
		String studentDepartment = sc.nextLine();
		System.out.print("학생 이메일 주소: ");
		String studentEmail = sc.nextLine();
		System.out.println("해당 학생정보를 추가하시겠습니까?");
		
		if (printSubMenu().equals("1")) {
			try {
				String sql = "" +
						"INSERT INTO FSTUDENTS (studentId, studentname, studentpassword, studentdepartment, studentmail) " +
						"VALUES (?, ?, ?, ?, ?)";
				//PreparedStatement 얻기 및 값 지정
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, studentId);			 // varchar2
				pstmt.setString(2, studentName);    	// varchar2
				pstmt.setString(3, studentPassword);		 // varchar2
				pstmt.setString(4, studentDepartment);    // varchar2
				pstmt.setString(5, studentEmail);		 // varchar2

				//SQL문 실행
				int rows = pstmt.executeUpdate();
				
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				exit();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
		}
		studentManagement();
	}
		
	private void searchStudent() {
		System.out.println("[FakeSchool 학생 조회] 교수: " +ploginId);
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("[조회할 학생 정보 입력]");
		System.out.print("학생 학번: ");
		String studentId = sc.nextLine();
		System.out.println("----------------------------------------------------------------------------");
		
		// FSTUDENTS테이블에서 studentId에 맞는 게시물을 가져와서 출력
		String sql = "" +
				"SELECT studentId, studentname, studentpassword, studentdepartment, studentmail	" +
				"FROM FSTUDENTS WHERE studentId = ?";
			
		try {
			// PreparedStatement 얻기 및 값 지정
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, studentId);
			
			// select문 실행
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {	// cursor 포인트가 1행 이동
				System.out.println("학번: " + rs.getString("studentId"));
				System.out.println("이름: " + rs.getString("studentname"));
				System.out.println("비밀번호: " + rs.getString("studentpassword"));
				System.out.println("학과: " + rs.getString("studentdepartment"));
				System.out.println("이메일: " + rs.getString("studentmail"));
				System.out.println("------------------------------------");
				} else {
					System.out.println("해당 학번에 해당하는 학생이 존재하지 않습니다.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				exit();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
					
		studentManagement();
	}
	
	private void updateStudent() {
		System.out.println("[FakeSchool 학생 정보 수정] 교수: " +ploginId);
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("[수정할 학생 학번 입력]");
		System.out.print("학생 학번: ");
		String studentId = sc.nextLine();
		
		// 기존 정보를 저장할 변수
	    String currentStudentName = null;
	    String currentStudentPassword = null;
	    String currentStudentDepartment = null;
	    String currentStudentEmail = null;
		
	    try {
			String sql = "" +
					"SELECT studentname, studentpassword, studentdepartment, studentmail " +
					"FROM FSTUDENTS WHERE studentId = ?";
		
			PreparedStatement pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, studentId);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	currentStudentName = rs.getString("studentname");
	        	currentStudentPassword = rs.getString("studentpassword");
	        	currentStudentDepartment = rs.getString("studentdepartment");
	        	currentStudentEmail = rs.getString("studentmail");
	        }
	        rs.close();
	        pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
		
		
		System.out.println("[수정할 학생 정보 입력](변경하지 않으려면 엔터를 누르세요)");
		System.out.print("학생 이름: ");;
		String input = sc.nextLine().trim();
		String studentName = input.isEmpty() ? currentStudentName : input;
		System.out.print("학생 비밀번호: ");
		input = sc.nextLine().trim();
		String studentPassword = input.isEmpty() ? currentStudentPassword : input;
		System.out.print("학생 학과: ");
		input = sc.nextLine().trim();
		String studentDepartment = input.isEmpty() ? currentStudentDepartment : input;
		System.out.print("학생 이메일 주소: ");
		input = sc.nextLine().trim();
		String studentEmail = input.isEmpty() ? currentStudentEmail : input;
		System.out.println("해당 학생의 정보를 수정하시겠습니까?");
		
		if (printSubMenu().equals("1")) {
			try {
				String sql2 = "" +
					"SELECT studentId " +
					"FROM FSTUDENTS WHERE studentId = ?";
				PreparedStatement pstmt2 = conn.prepareStatement(sql2);
				pstmt2.setString(1, studentId);		 // varchar2
				ResultSet rs2 = pstmt2.executeQuery();
				if (rs2.next()) {
					String sql3 = new StringBuilder()
							.append("UPDATE                    ")
							.append("	FSTUDENTS              ")
							.append("SET                       ")
							.append("	studentname = ?,  	   ")	// 1
							.append("	studentpassword = ?,   ")	// 2
							.append("	studentdepartment = ?, ")	// 3
							.append("	studentmail = ?  	   ")	// 4
							.append("WHERE                     ")
							.append("	studentId = ?		   ")	// 5
							.toString();
					//PreparedStatement 얻기 및 값 지정
					PreparedStatement pstmt3 = conn.prepareStatement(sql3);
					pstmt3.setString(1, studentName);		 // varchar2
					pstmt3.setString(2, studentPassword);    // varchar2
					pstmt3.setString(3, studentDepartment);		 // varchar2
					pstmt3.setString(4, studentEmail);   		 // varchar2
					pstmt3.setString(5, studentId);   		 // varchar2
					
					//SQL문 실행
					int rows = pstmt3.executeUpdate();
					pstmt3.close();
				} else {
					System.out.println("해당 학번에 해당하는 학생 정보가 존재하지 않습니다.");
				}
				pstmt2.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
				exit();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
		}
		studentManagement();
	}
	
	private void deleteStudent() {
		System.out.println("[FakeSchool 학생 정보 삭제]  교수: " + ploginId);
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("[삭제할 학생 정보 입력]");
		System.out.print("학생 학번: ");
		String studentId = sc.nextLine();
		System.out.println("해당 학생 정보를 삭제하시겠습니까?");
		
		if (printSubMenu().equals("1")) {
			try {
				String sql2 = "" +
						"SELECT studentId " +
						"FROM FSTUDENTS WHERE studentId = ?";
					PreparedStatement pstmt2 = conn.prepareStatement(sql2);
					pstmt2.setString(1, studentId);		 // varchar2
					ResultSet rs = pstmt2.executeQuery();
					if (rs.next()) {
						String sql = "DELETE FROM FSTUDENTS WHERE studentId = ?";
						PreparedStatement pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, studentId);
						pstmt.executeUpdate();
						rs.close();
						pstmt.close();
					} else {
						System.out.println("해당 학번에 해당하는 학생이 존재하지 않습니다.");
					}
				pstmt2.close();	
			} catch (SQLException e) {
				e.printStackTrace();
				exit();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
		}
		studentManagement();
	}
	
	private void proffessorManagement() {
		System.out.println("[FakeSchool 교수 관리] 교수: " +ploginId);
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("| 1.교수 정보 등록 | 2.교수 조회 | 3.교수 정보 수정 | 4.교수 정보 삭제 | 5.취소 |");
		System.out.println("----------------------------------------------------------------------------");
		System.out.print("메뉴선택: ");
		System.out.println();
		
		String menuNo = sc.nextLine();
		switch(menuNo) {
		case "1":
			addProffessor();
			break;
		case "2":
			searchProffessor();
			break;
		case "3":
			updateProffessor();
			break;
		case "4":
			deleteProffessor();
			break;
		case "5":
			mainMenu();
			break;
		default:
			System.out.println("잘못된 입력입니다. 다시 선택해 주세요.");
		}
	}

	private void addProffessor() {
		System.out.println("[FakeSchool 교수 정보 등록] 교수: " +ploginId);
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("[등록할 교수 정보 입력]");
		System.out.print("아이디: ");
		String professorId = sc.nextLine();
		System.out.print("이름: ");
		String professorName = sc.nextLine();
		System.out.print("비밀번호: ");
		String professorPassword = sc.nextLine();
		System.out.print("학과: ");
		String professorDepartment = sc.nextLine();
		System.out.print("이메일 주소: ");
		String professorEmail = sc.nextLine();
		System.out.println("해당 교수 정보를 추가하시겠습니까?");
		
		if (printSubMenu().equals("1")) {
			try {
				String sql = "" +
						"INSERT INTO FPROFESSOR (professorId, professorname, professorpassword, professordepartment, professormail) " +
						"VALUES (?, ?, ?, ?, ?)";
				//PreparedStatement 얻기 및 값 지정
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, professorId);			 // varchar2
				pstmt.setString(2, professorName);    	// varchar2
				pstmt.setString(3, professorPassword);		 // varchar2
				pstmt.setString(4, professorDepartment);    // varchar2
				pstmt.setString(5, professorEmail);		 // varchar2

				//SQL문 실행
				int rows = pstmt.executeUpdate();
				
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				exit();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
		}
		proffessorManagement();
	}

	private void searchProffessor() {
		System.out.println("[FakeSchool 교수 조회] 교수: " +ploginId);
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("[조회할 교수 정보 입력]");
		System.out.print("교수 이름: ");
		String professorName = sc.nextLine();
		System.out.println("----------------------------------------------------------------------------");
		
		// FPROFESSOR테이블에서 professorname에 맞는 게시물을 가져와서 출력
		String sql = "" +
				"SELECT professorId, professorname, professorpassword, professordepartment, professormail	" +
				"FROM FPROFESSOR WHERE professorname = ?";
			
		try {
			// PreparedStatement 얻기 및 값 지정
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, professorName);
			
			// select문 실행
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {	// cursor 포인트가 1행 이동
				System.out.println("아이디: " + rs.getString("professorId"));
				System.out.println("이름: " + rs.getString("professorname"));
				System.out.println("비밀번호: " + rs.getString("professorpassword"));
				System.out.println("학과: " + rs.getString("professordepartment"));
				System.out.println("이메일: " + rs.getString("professormail"));
				System.out.println("------------------------------------");
				} else {
					System.out.println("해당 이름의 교수가 존재하지 않습니다.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				exit();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
					
		proffessorManagement();
	}
	
	private void updateProffessor() {
		System.out.println("[FakeSchool 교수 정보 수정] 교수: " +ploginId);
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("[수정할 교수 이름 입력]");
		System.out.print("교수 이름: ");
		String professorName = sc.nextLine();
		
		// 기존 정보를 저장할 변수
		String currentProffessorId = null;
	    String currentProffessorName = null;
	    String currentProffessorPassword = null;
	    String currentProffessorDepartment = null;
	    String currentProffessorEmail = null;
		
	    try {
			String sql = "" +
					"SELECT professorId, professorname, professorpassword, professordepartment, professormail " +
					"FROM FPROFESSOR WHERE professorname = ?";
		
			PreparedStatement pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, professorName);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	currentProffessorId = rs.getString("professorId");
	        	currentProffessorName = rs.getString("professorname");
	        	currentProffessorPassword = rs.getString("professorpassword");
	        	currentProffessorDepartment = rs.getString("professordepartment");
	        	currentProffessorEmail = rs.getString("professormail");
	        }
	        rs.close();
	        pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
		
		System.out.println("[수정할 교수 정보 입력](변경하지 않으려면 엔터를 누르세요)");
		System.out.print("교수 아이디: ");
		String input = sc.nextLine().trim();
		String professorId = input.isEmpty() ? currentProffessorId : input;
		System.out.print("교수 이름: ");
		input = sc.nextLine().trim();
		String professorReName = input.isEmpty() ? currentProffessorName : input;
		System.out.print("교수 비밀번호: ");
		input = sc.nextLine().trim();
		String professorPassword = input.isEmpty() ? currentProffessorPassword : input;
		System.out.print("교수 학과: ");
		input = sc.nextLine().trim();
		String professorDepartment = input.isEmpty() ? currentProffessorDepartment : input;
		System.out.print("교수 이메일 주소: ");
		input = sc.nextLine().trim();
		String professorEmail = input.isEmpty() ? currentProffessorEmail : input;
		System.out.println("해당 교수의 정보를 수정하시겠습니까?");
		
		if (printSubMenu().equals("1")) {
			try {
				String sql2 = "" +
					"SELECT professorname " +
					"FROM FPROFESSOR WHERE professorname = ?";
				PreparedStatement pstmt2 = conn.prepareStatement(sql2);
				pstmt2.setString(1, professorName);		 // varchar2
				ResultSet rs = pstmt2.executeQuery();
				if (rs.next()) {
					String sql = new StringBuilder()
							.append("UPDATE                  	   ")
							.append("	FPROFESSOR           	   ")
							.append("SET                     	   ")
							.append("	professorId = ?,  	 	   ")	// 1
							.append("	professorname = ?,  	   ")	// 2
							.append("	professorpassword = ?,	   ")	// 3
							.append("	professordepartment = ?,   ")	// 4
							.append("	professormail = ?  	 	   ")	// 5
							.append("WHERE                  	   ")
							.append("	professorname = ?		   ")	// 6
							.toString();
					
					//PreparedStatement 얻기 및 값 지정
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, professorId);		 // varchar2
					pstmt.setString(2, professorReName);		 // varchar2
					pstmt.setString(3, professorPassword);    // varchar2
					pstmt.setString(4, professorDepartment);	// varchar2
					pstmt.setString(5, professorEmail);   		 // varchar2
					pstmt.setString(6, professorName);   		 // varchar2
					
					//SQL문 실행
					int rows = pstmt.executeUpdate();
					rs.close();
					pstmt.close();
				} else {
					System.out.println("해당 이름의 교수 정보가 존재하지 않습니다.");
				}
				pstmt2.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
				exit();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
		}
		proffessorManagement();
	}
	
	private void deleteProffessor() {
		System.out.println("[FakeSchool 교수 정보 삭제]  교수: " + ploginId);
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("[삭제할 교수 정보 입력]");
		System.out.print("교수 이름: ");
		String professorName = sc.nextLine();
		System.out.println("해당 교수 정보를 삭제하시겠습니까?");
		
		if (printSubMenu().equals("1")) {
			try {
				String sql2 = "" +
						"SELECT professorname " +
						"FROM FPROFESSOR WHERE professorname = ?";
					PreparedStatement pstmt2 = conn.prepareStatement(sql2);
					pstmt2.setString(1, professorName);		 // varchar2
					ResultSet rs = pstmt2.executeQuery();
					if (rs.next()) {
						String sql = "DELETE FROM FPROFESSOR WHERE professorname = ?";
						PreparedStatement pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, professorName);
						pstmt.executeUpdate();
						rs.close();
						pstmt.close();
					} else {
						System.out.println("해당 이름의 교수가 존재하지 않습니다.");
					}
				pstmt2.close();	
			} catch (SQLException e) {
				e.printStackTrace();
				exit();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
		}
		proffessorManagement();
	}
	
	private void dropout() {
		System.out.println("[FakeSchool 자퇴 신청]  학생: " + sloginId);
		System.out.println("----------------------------------------------------------------------------");
		String studentId = sloginId;
		System.out.println("정말로 자퇴 신청을 하시겠습니까?");
		
		if (printSubMenu().equals("1")) {
			try {
					String sql = "DELETE FROM FSTUDENTS WHERE studentId = ?";
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, studentId);
					pstmt.executeUpdate();
					pstmt.close();
					
			} catch (SQLException e) {
				e.printStackTrace();
				exit();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
		}
		System.out.println("자퇴 신청이 완료되었습니다.");
		sloginId = null;
		mainMenu();
	}
	
	// fakeschool 종료 기능
	private void exit() {
		System.out.println("FakeSchool 온라인 서비스가 정상 종료되었습니다.");
		System.exit(0);
	}

	// 실행여부 확인 기능
	private String printSubMenu() {
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("| 1.Ok | 2.Cancel |");
		System.out.print("메뉴선택: ");
		return sc.nextLine();
	}
	
	public static void main(String[] args) {
		FakeSchool fakeschool = new FakeSchool();
		fakeschool.mainMenu();
	}

}
