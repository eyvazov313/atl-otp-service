package academy.atl.otp.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OtpGenerator {

  public Integer generateOtp(){
    Random random = new Random();
    int otp = random.nextInt(1000, 9999);
    return otp;
  }
}
