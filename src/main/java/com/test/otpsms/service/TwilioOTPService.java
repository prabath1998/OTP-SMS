package com.test.otpsms.service;

import com.test.otpsms.config.TwilioConfig;
import com.test.otpsms.dto.OtpStatus;
import com.test.otpsms.dto.PasswordResetRequestDto;
import com.test.otpsms.dto.PasswordResetResponseDto;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.DecimalFormat;
import java.util.Random;

@Service
public class TwilioOTPService {

    @Autowired
    private TwilioConfig twilioConfig;

    public Mono<PasswordResetResponseDto> sendOTPForPasswordReset(PasswordResetRequestDto passwordResetRequestDto){
        PasswordResetResponseDto passwordResetResponseDto = null;
        try {
            PhoneNumber to = new PhoneNumber(passwordResetRequestDto.getPhoneNumber());
            PhoneNumber from = new PhoneNumber(twilioConfig.getTrialNumber());
            String otp = generateOTP();
            String otpMessage = "Dear customer , Your OTP is ##" + otp + "##. Use this passcode to complete your transaction. Thank You.";

            Message message = Message
                    .creator(to,
                            from, otpMessage)
                    .create();
            passwordResetResponseDto = new PasswordResetResponseDto(OtpStatus.DELIVERED, otpMessage);
        }catch (Exception ex){
            passwordResetResponseDto = new PasswordResetResponseDto(OtpStatus.FAILED, ex.getMessage());

        }
        return Mono.just(passwordResetResponseDto);
    }



    //set 6 digit otp
    private String generateOTP(){
        return new DecimalFormat("000000").format(new Random().nextInt(999999));
    }
}
