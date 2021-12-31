package com.umbrella.training.mvvm;

import static org.junit.Assert.assertEquals;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ModelTest {

    private static String USER_JSON = "{\"photo\":\"https://gimg1.baidu"
            + ".com/searchbox_feed/app=2001&g=4n&n=0&q=100&refer=http%3A%2F%2Fwww.baidu"
            + ".com&size=f159_159&src=https%3A%2F%2Fuser-center.cdn.bcebos.com%2Fhead%2Fraw%2Fuc.101.f8e4c7f6"
            + ".x1pWp2aluUyw4GymuCy84g%3Fx-bce-process%3Dimage%2Fresize%2Cm_lfit%2Cw_200%2Ch_200%26autime%3D25212?sec"
            + "=0&t=cd60a85a88b601fa0bfdd20e02c64cdc\",\"name\":{\"text\":\"大河网\"},\"desc\":{\"text\":\"大河网官方帐号\"},"
            + "\"create_time\":{\"text\":\"1636498106\"},\"vtype\":\"2\",\"v_url\":\"https://b.bdstatic"
            + ".com/searchbox/image/cmsuploader/20200731/1596177792491404.png\"}";

    private static String FAVOURITE_JSON = "{\"count\":\"626\",\"tplid\":\"activity_text\","
            + "\"ukey\":\"baiduapp_ugc://dt_3332965558174427129\",\"url\":\"https://mbd.baidu"
            + ".com/newspage/data/dtlandingwise?sourceFrom=dtlandimmersive&nid=dt_3332965558174427129\",\"img\":\"\","
            + "\"source\":\"胡锡进\",\"tag\":\"\",\"imagecount\":0,\"extdata\":{\"landingSource\":\"1\","
            + "\"ubcjson\":{\"page\":\"\",\"source\":\"dt_landing\",\"value\":\"\"}}}";
    @Test
    public void testUserJsonAndModel() {

        try {
            JSONObject userJObject = new JSONObject(USER_JSON);
            AuthorData authorData = Author.fromJson(userJObject);
            assertEquals(authorData != null ? authorData.getCreateTime() : null, "1636498106");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFavouriteJsonAndModel() {
        try {
            JSONObject favouriteJObject = new JSONObject(FAVOURITE_JSON);
            FavouriteData favouriteData = Favourite.fromJson(favouriteJObject);
            assertEquals(favouriteData != null ? favouriteData.getUkey() : null, "baiduapp_ugc://dt_3332965558174427129");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
