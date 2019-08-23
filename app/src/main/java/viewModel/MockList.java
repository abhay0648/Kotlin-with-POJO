package viewModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//Get all data in utils model class
public class MockList {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;

    public Location location = new Location();

    public void setOptions(Location location) {
        this.location = location;
    }

    public Location getOptions() {
        return location;
    }

    public MockList(String id, String description, String imageUrl) {
        this.id = id;
        this.description = description;
        this.imageUrl = imageUrl;
    }


    public MockList() {
        this.id = id;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getID() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setID(String id) {
        this.id = id;
    }

    public class Location{
        public Location(String lat, String lng, String address) {
            this.lat = lat;
            this.lng = lng;
            this.address = address;
        }

        public Location() {
        }

        @SerializedName("lat")
        @Expose
        private String lat;

        @SerializedName("lng")
        @Expose
        private String lng;

        @SerializedName("address")
        @Expose
        private String address;

        public String getLat() {
            return lat;
        }

        public String getLng() {
            return lng;
        }

        public String getAddress() {
            return address;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public void setAddress(String address) {
            this.address = address;
        }

    }

}
