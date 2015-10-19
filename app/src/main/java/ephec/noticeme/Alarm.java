    package ephec.noticeme;

    import java.util.Date;

    /**
     * Created by Thrag on 29-09-15.
     */
    public class Alarm
    {
        private int id;
        private int groupId;
        private String modificationDate;
        private String title;
        private String description;
        private double latitude;
        private double longitude;
        private String alarmDate;

        public Alarm() {
        }

        public Alarm(int id, String alarmDate, float longitude, float latitude, String description, String title, String modificationDate, int groupId) {
            this.id = id;
            this.alarmDate = alarmDate;
            this.longitude = longitude;
            this.latitude = latitude;
            this.description = description;
            this.title = title;
            this.modificationDate = modificationDate;
            this.groupId = groupId;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAlarmDate() {
            return alarmDate;
        }

        public void setAlarmDate(String alarmDate) {
            this.alarmDate = alarmDate;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getModificationDate() {
            return modificationDate;
        }

        public void setModificationDate(String modificationDate) {
            this.modificationDate = modificationDate;
        }

        public int getGroupId() {
            return groupId;
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
        }

    }
