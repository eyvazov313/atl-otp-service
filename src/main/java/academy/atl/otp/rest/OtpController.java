package academy.atl.otp.rest;

import academy.atl.otp.dao.enums.OtpStatus;
import academy.atl.otp.service.OtpService;
import academy.atl.otp.util.dto.SendOtpRequestDto;
import academy.atl.otp.util.dto.SendOtpResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/otp")
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/request")
    public String sendOtp(@RequestBody SendOtpRequestDto requestDto){
        return otpService.sendOtp(requestDto).toString();
    }

    @PostMapping("/verify")
    public SendOtpResponseDto verifyOtp(@RequestParam String msisdn, @RequestParam Integer otp){
        return otpService.verifyOtp(msisdn, otp);
    }
}
