package seedu.canoe.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.canoe.commons.core.Messages.MESSAGE_STUDENTS_NOT_FOUND;
import static seedu.canoe.commons.core.Messages.MESSAGE_TRAININGS_NOT_FOUND;
import static seedu.canoe.model.Model.PREDICATE_SHOW_ALL_STUDENTS;

import java.util.List;
import java.util.logging.Logger;

import seedu.canoe.commons.core.LogsCenter;
import seedu.canoe.commons.core.index.Index;
import seedu.canoe.logic.commands.exceptions.CommandException;
import seedu.canoe.model.Model;
import seedu.canoe.model.student.AnyMatchPredicateList;
import seedu.canoe.model.student.Attendance;
import seedu.canoe.model.student.Student;
import seedu.canoe.model.training.Training;

public class UnmarkAttendanceCommand extends Command {

    public static final Logger LOGGER = LogsCenter.getLogger(UnmarkAttendanceCommand.class);

    public static final String COMMAND_WORD = "unmark-attendance";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Gets all students in the training session"
            + " whose Ids corresponds to the specified Ids and unmark them as attended the training session.\n"
            + "Parameters: ID [MORE_IDS]...\n"
            + "Example: " + COMMAND_WORD + " 2 id/1,4,19";

    public static final String MESSAGE_NO_STUDENTS_SPECIFIED = "At least one student to be added must be specified.";
    public static final String MESSAGE_INVALID_STUDENT_MARKED = "Some students do not have specified"
            + " training session scheduled!";
    public static final String MESSAGE_MARK_AS_ATTENDED_SUCCESS = "Unmarked these students as attended: %1$s!";

    private final Index trainingIndex;
    private final AnyMatchPredicateList predicates;

    /**
     * Constructs a new UnmarkAttendanceCommand object.
     *
     * @param trainingIndex index of training.
     * @param predicates list of predicates.
     */
    public UnmarkAttendanceCommand(Index trainingIndex, AnyMatchPredicateList predicates) {
        this.trainingIndex = trainingIndex;
        this.predicates = predicates;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        LOGGER.info("=============================[ Executing UnmarkAttendanceCommand ]===========================");
        requireNonNull(model);
        List<Training> lastShownList = model.getFilteredTrainingList();

        if (trainingIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(MESSAGE_TRAININGS_NOT_FOUND);
        }

        model.updateFilteredStudentList(predicates);

        if (model.getFilteredStudentList().isEmpty()) {
            LOGGER.warning("Ids match zero students.");
            model.updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);
            throw new CommandException(MESSAGE_STUDENTS_NOT_FOUND);
        }

        List<Student> attendedStudents = model.getFilteredStudentList();

        Training training = lastShownList.get(trainingIndex.getZeroBased());

        Attendance unattendedAttendance = new Attendance(training.getDateTime());
        Attendance attendedAttendance = new Attendance(training.getDateTime());
        attendedAttendance.unattends();

        if (!studentsHaveAttendance(unattendedAttendance, attendedStudents)) {
            LOGGER.warning("Some students do not contain training session");
            return new CommandResult(MESSAGE_INVALID_STUDENT_MARKED);
        }

        for (Student student : attendedStudents) {
            student.attendTrainingSession(unattendedAttendance, attendedAttendance);
        }
        String result = getStudentIdsAsString(attendedStudents);

        model.updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);
        return new CommandResult(String.format(MESSAGE_MARK_AS_ATTENDED_SUCCESS, result));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UnmarkAttendanceCommand // instanceof handles nulls
                && predicates.equals(((UnmarkAttendanceCommand) other).predicates)); // state check
    }

    /**
     * Checks the list of students and returns whether all the students have the attendance.
     *
     * @param attendance to be checked for.
     * @param studentsToCheck list of students to check.
     * @return true if students have training session scheduled, false if otherwise.
     */
    public boolean studentsHaveAttendance(Attendance attendance, List<Student> studentsToCheck) {
        assert attendance != null;
        for (Student student: studentsToCheck) {
            if (!student.containsAttendance(attendance)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a String with the IDs of the students.
     *
     * @return String with Unique Ids of Students.
     */
    public String getStudentIdsAsString(List<Student> students) {
        String result = "";
        for (Student student : students) {
            result += student.getId() + " ";
        }
        result = result.trim();
        return result;
    }
}