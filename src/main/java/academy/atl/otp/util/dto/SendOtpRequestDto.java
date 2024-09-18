package academy.atl.otp.util.dto;

import academy.atl.otp.dao.enums.OtpType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class SendOtpRequestDto {
  private String msisdn;
  private OtpType otpType;
}
