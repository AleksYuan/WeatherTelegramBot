package bot;


import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.util.List;

@Getter
@Setter
public class Client{
    private int id;
    private String chatId;
    private String userName;
    private String city;
    private byte hours;
    private byte minutes;
}
