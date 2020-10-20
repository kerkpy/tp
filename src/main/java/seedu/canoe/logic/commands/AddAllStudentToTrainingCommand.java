package seedu.canoe.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.canoe.commons.core.LogsCenter;
import seedu.canoe.commons.core.Messages;
import seedu.canoe.commons.core.index.Index;
import seedu.canoe.logic.commands.exceptions.CommandException;
import seedu.canoe.model.Model;
import seedu.canoe.model.student.Id;
import seedu.canoe.model.student.Student;
import seedu.canoe.model.student.Training;

public class AddAllStudentToTrainingCommand extends Command {

    public static final Logger LOGGER = LogsCenter.getLogger(AddAllStudentToTrainingCommand.class);

    public static final String COMMAND_WORD = "ts-addall";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds all students"
            + " from the current displayed student list to the specified training session\n"
            + "Parameters: Training_Session-ID"
            + "\nExample: "
            + COMMAND_WORD + "1";

    public static final String MESSAGE_ADD_STUDENT_SUCCESS = "Added available students: %1$s";
    public static final String MESSAGE_NO_STUDENTS = "At least one student must be be available for the training.";

    private final Index index;

    /**
     * Creates an AddAllStudentToTrainingCommand to add all students to the specified {@code Training}
     */
    public AddAllStudentToTrainingCommand(Index index) {
        requireNonNull(index);
        this.index = index;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        LOGGER.info("==========================[ Executing AddAllStudentToTrainingCommand ]==========================");

        requireNonNull(model);

        // Gets the current displayed lists
        List<Training> trainingList = model.getFilteredTrainingList();
        List<Student> studentList = model.getFilteredStudentList();

        // Checks if training index is invalid
        if (index.getZeroBased() >= trainingList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_TRAINING_DISPLAYED_INDEX);
        }

        // Checks if student list is empty
        if (studentList.isEmpty()) {
            throw new CommandException(MESSAGE_NO_STUDENTS);
        }

        Training training = trainingList.get(index.getZeroBased());
        Training editedTraining = training.cloneTraining();
        LocalDateTime trainingDateTime = training.getDateTime();
        List<Student> addedStudents = new ArrayList<>();

        studentList.stream()
                .filter(student -> student.isAvailableAtDateTime(trainingDateTime)
                    && !student.hasTrainingAtDateTime(trainingDateTime)
                    && !training.hasStudent(student))
                .forEach(student -> {
                    addStudentToTraining(editedTraining, student, model);
                    addedStudents.add(student);
                });

        model.setTraining(training, editedTraining);
        Optional<String> addedStudentsMessage = getStudentsMessage(addedStudents);
        if (addedStudentsMessage.isEmpty()) {
            throw new CommandException(MESSAGE_NO_STUDENTS);
        }
        return new CommandResult(String.format(MESSAGE_ADD_STUDENT_SUCCESS,
                addedStudentsMessage.get()));
    }

    private void addStudentToTraining(Training training, Student student, Model model) {
        training.addStudent(student);
        Student editedStudent = student.cloneStudent();
        editedStudent.addTraining(training.getDateTime());
        model.setStudentInUniqueStudentList(student, editedStudent);
    }

    private Optional<String> getStudentsMessage(List<Student> students) {
        return students.stream()
                .map(Student::getId)
                .map(Id::toString)
                .reduce((id1, id2) -> id1 + ", " + id2);
    }
}
