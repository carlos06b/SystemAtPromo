package model;

import java.time.LocalDateTime;

public class Request {
        private int id;
        private int id_UserRH;
        private int id_UserFin;
        private int id_Promoter;
        private String message;
        private String status;
        private LocalDateTime date;

        public Request(int id, LocalDateTime date, int id_Promoter, int id_UserFin, int id_UserRH, String message, String status) {
            this.id = id;
            this.date = date;
            this.id_Promoter = id_Promoter;
            this.id_UserFin = id_UserFin;
            this.id_UserRH = id_UserRH;
            this.message = message;
            this.status = status;
        }

        public int getId() {
        return id;
        }

        public void setId(int id) {
        this.id = id;
        }

        public LocalDateTime getDate() {
            return date;
        }

        public void setDate(LocalDateTime date) {
            this.date = date;
        }

        public int getId_Promoter() {
            return id_Promoter;
        }

        public void setId_Promoter(int id_Promoter) {
            this.id_Promoter = id_Promoter;
        }

        public int getId_UserFin() {
            return id_UserFin;
        }

        public void setId_UserFin(int id_UserFin) {
            this.id_UserFin = id_UserFin;
        }

        public int getId_UserRH() {
            return id_UserRH;
        }

        public void setId_UserRH(int id_UserRH) {
            this.id_UserRH = id_UserRH;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
