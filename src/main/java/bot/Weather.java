package bot;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Weather {


    private String dt;
    private String timezone;
    private String id;
    private String name;
    private String cod;

    private Map<String ,String> coord;
    private ArrayList<Map<String, String>> weather;
    private String base;
    private Map<String, String> main;
    private String visibility;
    private Map<String, String> snow;
    private Map<String, String> wind;
    private Map<String, String> clouds;
    private Map<String, String> sys;
}
