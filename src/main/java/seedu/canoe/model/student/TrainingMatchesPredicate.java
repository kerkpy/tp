package seedu.canoe.model.student;

import java.util.List;
import java.util.function.Predicate;

/**
 * Tests that a {@code Training}'s {@code dateTime} matches any of the keywords given.
 */
public class TrainingMatchesPredicate implements Predicate<Training> {
    private final List<Attend> trainingAttendances;

    public TrainingMatchesPredicate(List<Attend> trainingAttendances) {
        this.trainingAttendances = trainingAttendances;
    }

    @Override
    public boolean test(Training training) {
        return trainingAttendances.stream()
                .anyMatch(trainingAttending -> trainingAttending.getTrainingTime().equals(training.getDateTime()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof TrainingMatchesPredicate // instanceof handles nulls
                && trainingAttendances.equals(((TrainingMatchesPredicate) other).trainingAttendances)); // state check
    }

}
