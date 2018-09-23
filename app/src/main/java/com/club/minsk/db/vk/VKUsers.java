package com.club.minsk.db.vk;

import java.text.SimpleDateFormat;
import java.util.List;

public class VKUsers {
    Object error;
    public List<VKUser> response;

    public class VKUser {
        public Long id;
        public String first_name;
        public String last_name;
        public String domain;
        public Long sex;
        public Long relation;
        public String bdate;
        public String mobile_phone;
        public CropPhoto crop_photo;
        public String photo_max;

        public class CropPhoto {
            public Photo photo;
        }
        public class Photo{
            public String photo_807;
            public Long width;
            public Long height;
        }

        public Long getUnixBirthday(){
            Long birthday = null;
            try {
                birthday = (new SimpleDateFormat("dd.MM.yyyy").parse(bdate).getTime() / 1000);
            } catch (Exception ignore) {
            }
            return birthday;
        }
    }

}
