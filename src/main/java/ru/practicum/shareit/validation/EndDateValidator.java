package ru.practicum.shareit.validation;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EndDateValidator implements ConstraintValidator<ValidEndDate, BookingDto> {

    @Override
    public boolean isValid(BookingDto booking, ConstraintValidatorContext constraintValidatorContext) {
        return booking.getEnd().isAfter(booking.getStart());
    }
}
