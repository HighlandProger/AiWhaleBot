package ru.rusguardian.service.data;

import lombok.extern.slf4j.Slf4j;
import ru.rusguardian.domain.UserDataDto;
import ru.rusguardian.service.ai.dto.stable_diffusion.img2video.VideoModel;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
public class UserDataDtoService {

    private static Map<Long, UserDataDto> dtoMap = new HashMap<>();

    public static void addImageUrl(Long id, String imageUrl){
        Optional<UserDataDto> dtoOptional = getDtoById(id);
        if(dtoOptional.isPresent()){
            dtoOptional.get().setImageUrl(imageUrl);
            return;
        }
        UserDataDto dto = new UserDataDto();
        dto.setImageUrl(imageUrl);
        dtoMap.put(id, dto);
    }

    public static void addPrompt(Long id, String prompt){
        Optional<UserDataDto> dtoOptional = getDtoById(id);
        if(dtoOptional.isPresent()){
            dtoOptional.get().setPrompt(prompt);
            return;
        }
        UserDataDto dto = new UserDataDto();
        dto.setPrompt(prompt);
        dtoMap.put(id, dto);
    }

    public static void addVideoModel(Long id, VideoModel model){
        Optional<UserDataDto> dtoOptional = getDtoById(id);
        if(dtoOptional.isPresent()){
            dtoOptional.get().setVideoModel(model);
            return;
        }
        UserDataDto dto = new UserDataDto();
        dto.setVideoModel(model);
        dtoMap.put(id, dto);
    }

    private static Optional<UserDataDto> getDtoById(Long id){
        return Optional.ofNullable(dtoMap.get(id));
    }

    public static UserDataDto getDtoByIdAndRemove(Long id){
        Optional<UserDataDto> dtoOptional = getDtoById(id);
        if(dtoOptional.isEmpty()){
            String msg = "UserDataDto is not present for id:" + id;
            log.error(msg);
            throw new NoSuchElementException(msg);
        }
        UserDataDto dto = dtoOptional.get();
        dtoMap.remove(dto.getId());
        return dto;
    }
}
