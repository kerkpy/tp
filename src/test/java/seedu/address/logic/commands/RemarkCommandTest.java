package seedu.address.logic.commands;

import org.junit.jupiter.api.Test;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.TypicalPersons;

import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

class RemarkCommandTest {

    private final String REMARK_STUB = "THIS IS A STUB";
    private Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());

    @Test
    void execute_addRemark_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(firstPerson).withRemark(REMARK_STUB).build();
        RemarkCommand remarkCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(editedPerson.getRemark().value));
        String expectedMessage = String.format(RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS, editedPerson);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        CommandTestUtil.assertCommandSuccess(remarkCommand, model, expectedMessage, expectedModel);
    }
}