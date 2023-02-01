package bot.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Client{

    private boolean setTime;
    private boolean setCity;
    private int id;
    private String chatId;
    private String userName;
    private String city;
    private byte hours;
    private byte minutes;

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", chatId='" + chatId + '\'' +
                ", userName='" + userName + '\'' +
                ", city='" + city + '\'' +
                ", hours=" + hours +
                ", minutes=" + minutes +
                '}';
    }
}
