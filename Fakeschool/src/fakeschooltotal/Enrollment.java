package fakeschooltotal;

import lombok.Data;

@Data
public class Enrollment {
	private String studentId;
	private String courseId;
	private String enrollmentSemester;
	private String enrollmentGrade;
	private int enrollmentScore;
	private double enrollmentPoint;
}
