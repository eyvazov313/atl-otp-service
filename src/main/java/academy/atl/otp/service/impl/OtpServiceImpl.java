package academy.atl.otp.service.impl;

import academy.atl.otp.dao.entity.OtpEntity;
import academy.atl.otp.dao.enums.OtpStatus;
import academy.atl.otp.dao.repository.OtpRepository;
import academy.atl.otp.service.OtpService;
import academy.atl.otp.util.OtpGenerator;
import academy.atl.otp.util.dto.SendOtpRequestDto;
import academy.atl.otp.util.dto.SendOtpResponseDto;
import java.time.Instant;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

  @Value("${otp.expiration}")
  private Integer EXPIRATION_TIME;
  @Value("${otp.sendOtpLimit}")
  private Integer SEND_OTP_LIMIT;
  private final OtpRepository otpRepository;
  private final OtpGenerator otpGenerator;
  @Override
  public SendOtpResponseDto sendOtp(SendOtpRequestDto sendOtpRequestDto) {
    var otpEntityOptional = otpRepository.findByMsisdn(sendOtpRequestDto.getMsisdn());
    if (otpEntityOptional.isPresent()){
      var entity = otpEntityOptional.get();
      if (OtpStatus.BLOCKED.equals(entity.getOtpStatus())){
          if (Instant.now().isAfter(entity.getExpirationTime())){
            sendOtpAgainAfterBlock(entity);
            return new SendOtpResponseDto(OtpStatus.SUCCESS);
          }
          else{
            return new SendOtpResponseDto(OtpStatus.BLOCKED);
          }
      }
      else{
        if (SEND_OTP_LIMIT < entity.getSendOtpCount()){
          makeUserBlocked(entity);
          return new SendOtpResponseDto(OtpStatus.BLOCKED);
        }
        else{
          sendOtpAgain(entity);
          return new SendOtpResponseDto(OtpStatus.SUCCESS);

        }
      }
    }
    sendOtpFirstTime(sendOtpRequestDto);
    return new SendOtpResponseDto(OtpStatus.SUCCESS);
  }

  @Override
  public SendOtpResponseDto verifyOtp(String msisdn, Integer otp) {
    var otpEntityOptional = otpRepository.findByMsisdn(msisdn);
    if(otpEntityOptional.isPresent()){
      var otpEntity = otpEntityOptional.get();
      if (OtpStatus.BLOCKED.equals(otpEntity.getOtpStatus())) {
        if (Instant.now().isAfter(otpEntity.getExpirationTime())) {
          sendOtpAgainAfterBlock(otpEntity);
          return new SendOtpResponseDto(OtpStatus.PENDING);
        } else {
          return new SendOtpResponseDto(OtpStatus.BLOCKED);
        }
      } else {
        if (otpEntity.getOtp().equals(otp)) {
          otpEntity.setOtpStatus(OtpStatus.SUCCESS);
          otpRepository.save(otpEntity);
          return new SendOtpResponseDto(OtpStatus.SUCCESS);
        } else {
          otpEntity.setAttemptCount(otpEntity.getAttemptCount() + 1);
          if (otpEntity.getAttemptCount() > SEND_OTP_LIMIT) {
            makeUserBlocked(otpEntity);
            return new SendOtpResponseDto(OtpStatus.BLOCKED);
          } else {
            otpRepository.save(otpEntity);
            return new SendOtpResponseDto(OtpStatus.FAIL);
          }
        }
      }
    } else {
      return null;
    }
  }

  private void makeUserBlocked(OtpEntity otpEntity){
    otpEntity.setOtpStatus(OtpStatus.BLOCKED);
    otpEntity.setSendOtpCount(otpEntity.getAttemptCount() + 1);
    otpEntity.setExpirationTime(Instant.now().plusMillis(EXPIRATION_TIME));
    otpRepository.save(otpEntity);
  }


  private void sendOtpAgain(OtpEntity otpEntity){
    var otpCode = otpGenerator.generateOtp();
    var expireTime = Instant.now().plusMillis(EXPIRATION_TIME);
    var sendOtpCount = otpEntity.getSendOtpCount() + 1;
    otpEntity.setOtp(otpCode);
    otpEntity.setOtpStatus(OtpStatus.PENDING);
    otpEntity.setExpirationTime(expireTime);
    otpEntity.setSendOtpCount(sendOtpCount);
    otpRepository.save(otpEntity);

  }
  private void sendOtpAgainAfterBlock(OtpEntity otpEntity){
    var otpCode = otpGenerator.generateOtp();
    var expireTime = Instant.now().plusMillis(EXPIRATION_TIME);

    otpEntity.setOtp(otpCode);
    otpEntity.setOtpStatus(OtpStatus.PENDING);
    otpEntity.setExpirationTime(expireTime);
    otpEntity.setAttemptCount(0);
    otpEntity.setSendOtpCount(1);
    otpRepository.save(otpEntity);
  }


  private void sendOtpFirstTime(SendOtpRequestDto sendOtpRequestDto){
    var otpCode = otpGenerator.generateOtp();
    var expireTime = Instant.now().plusMillis(EXPIRATION_TIME);

    var otpEntity = OtpEntity.builder()
        .otpType(sendOtpRequestDto.getOtpType())
        .otpStatus(OtpStatus.PENDING)
        .attemptCount(0)
        .sendOtpCount(1)
        .expirationTime(expireTime)
        .otp(otpCode)
        .msisdn(sendOtpRequestDto.getMsisdn())
        .build();

    otpRepository.save(otpEntity);
  }
}
