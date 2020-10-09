package seedu.address.logic.commands;

import static seedu.address.logic.commands.TrainingCommand.MESSAGE_SUCCESS_TRAINING;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalTraining.VALID_TRAINING;

import org.junit.jupiter.api.Test;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

public class TrainingCommandTest {

    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();
    private LocalDateTime VALID_DATETIME = VALID_TRAINING.getDateTime();

    //tests with a valid Training object.
    @Test
    public void execute_trainingAcceptedByModel_AddSuccessful() throws Exception {
        String expectedCommandResult = new TrainingCommand(VALID_DATETIME).execute(expectedModel).getFeedbackToUser();
        assertCommandSuccess(new TrainingCommand(VALID_TRAINING.getDateTime()), model, expectedCommandResult, expectedModel);
    }

    //tests with a invalid Training object with invalid month.
    @Test
    public void execute_trainingRejectedByModel_AddFailure() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
        assertThrows(DateTimeParseException.class, () -> new TrainingCommand(LocalDateTime.parse("2000-20-11 1900", formatter)));
    }

    //tests with a invalid Training object with wrong format.
    @Test
    public void execute_trainingRejectedByModel_AddFailure2() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
        assertThrows(DateTimeParseException.class, () -> new TrainingCommand(LocalDateTime.parse("2000-20-11", formatter)));
    }
}
