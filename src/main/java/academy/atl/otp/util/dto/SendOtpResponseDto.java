package academy.atl.otp.util.dto;

import academy.atl.otp.dao.enums.OtpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class SendOtpResponseDto {
  private OtpStatus status;
}
