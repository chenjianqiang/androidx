package com.cjq.androidx.bean;

import java.util.List;

public class DailyPicture {
    /**
     * images : [{"startdate":"20200628","fullstartdate":"202006281600","enddate":"20200629","url":"/th?id=OHR.ArganGoats_ZH-CN5346845518_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp","urlbase":"/th?id=OHR.ArganGoats_ZH-CN5346845518","copyright":"索维拉附近摩洛哥坚果树上的山羊，摩洛哥 (© Nizz/Shutterstock)","copyrightlink":"https://www.bing.com/search?q=%E7%B4%A2%E7%BB%B4%E6%8B%89&form=hpcapt&mkt=zh-cn","title":"","quiz":"/search?q=Bing+homepage+quiz&filters=WQOskey:%22HPQuiz_20200628_ArganGoats%22&FORM=HPQUIZ","wp":true,"hsh":"1d91cdf13a8282a02baa870e22e29ddc","drk":1,"top":1,"bot":1,"hs":[]},{"startdate":"20200627","fullstartdate":"202006271600","enddate":"20200628","url":"/th?id=OHR.FoggyCastle_ZH-CN6799694629_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp","urlbase":"/th?id=OHR.FoggyCastle_ZH-CN6799694629","copyright":"雾中的卡斯特诺城堡，法国佩里戈尔 (© Infografick/iStock/Getty Images Plus)","copyrightlink":"https://www.bing.com/search?q=%E5%8D%A1%E6%96%AF%E7%89%B9%E8%AF%BA%E5%9F%8E%E5%A0%A1&form=hpcapt&mkt=zh-cn","title":"","quiz":"/search?q=Bing+homepage+quiz&filters=WQOskey:%22HPQuiz_20200627_FoggyCastle%22&FORM=HPQUIZ","wp":true,"hsh":"57cdc93c46d865466bb2c1edc694a3f4","drk":1,"top":1,"bot":1,"hs":[]},{"startdate":"20200626","fullstartdate":"202006261600","enddate":"20200627","url":"/th?id=OHR.MtBaldoSantuario_ZH-CN2301293454_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp","urlbase":"/th?id=OHR.MtBaldoSantuario_ZH-CN2301293454","copyright":"Madonna della Corona教堂，意大利 (© Volodymyr Kalyniuk/Alamy)","copyrightlink":"https://www.bing.com/search?q=Madonna+della+Corona%E6%95%99%E5%A0%82&form=hpcapt&mkt=zh-cn","title":"","quiz":"/search?q=Bing+homepage+quiz&filters=WQOskey:%22HPQuiz_20200626_MtBaldoSantuario%22&FORM=HPQUIZ","wp":true,"hsh":"bb6983ee6f9b4f7a6c52593989322afa","drk":1,"top":1,"bot":1,"hs":[]},{"startdate":"20200625","fullstartdate":"202006251600","enddate":"20200626","url":"/th?id=OHR.AdansoniaGrandidieri_ZH-CN1154912052_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp","urlbase":"/th?id=OHR.AdansoniaGrandidieri_ZH-CN1154912052","copyright":"穆龙达瓦附近的格兰迪尔猴面包树林，马达加斯加 (© Thomas Marent/Minden Pictures)","copyrightlink":"https://www.bing.com/search?q=%E6%A0%BC%E5%85%B0%E8%BF%AA%E5%B0%94%E7%8C%B4%E9%9D%A2%E5%8C%85%E6%A0%91&form=hpcapt&mkt=zh-cn","title":"","quiz":"/search?q=Bing+homepage+quiz&filters=WQOskey:%22HPQuiz_20200625_AdansoniaGrandidieri%22&FORM=HPQUIZ","wp":true,"hsh":"57ec87b07836c7ffa447150d6505cc5c","drk":1,"top":1,"bot":1,"hs":[]},{"startdate":"20200624","fullstartdate":"202006241600","enddate":"20200625","url":"/th?id=OHR.duanwu2020_ZH-CN0965379603_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp","urlbase":"/th?id=OHR.duanwu2020_ZH-CN0965379603","copyright":"【今日端午】 (© su_pei/iStock/Getty Images Plus)","copyrightlink":"https://www.bing.com/search?q=%E7%AB%AF%E5%8D%88&form=hpcapt&mkt=zh-cn","title":"","quiz":"/search?q=Bing+homepage+quiz&filters=WQOskey:%22HPQuiz_20200624_duanwu2020%22&FORM=HPQUIZ","wp":true,"hsh":"b11f89d336163ce749e28b74f5226be0","drk":1,"top":1,"bot":1,"hs":[]}]
     * tooltips : {"loading":"Loading...","previous":"Previous image","next":"Next image","walle":"This image is not available to download as wallpaper.","walls":"Download this image. Use of this image is restricted to wallpaper only."}
     */

    private TooltipsBean tooltips;
    private List<ImagesBean> images;

    public TooltipsBean getTooltips() {
        return tooltips;
    }

    public void setTooltips(TooltipsBean tooltips) {
        this.tooltips = tooltips;
    }

    public List<ImagesBean> getImages() {
        return images;
    }

    public void setImages(List<ImagesBean> images) {
        this.images = images;
    }

    public static class TooltipsBean {
        /**
         * loading : Loading...
         * previous : Previous image
         * next : Next image
         * walle : This image is not available to download as wallpaper.
         * walls : Download this image. Use of this image is restricted to wallpaper only.
         */

        private String loading;
        private String previous;
        private String next;
        private String walle;
        private String walls;

        public String getLoading() {
            return loading;
        }

        public void setLoading(String loading) {
            this.loading = loading;
        }

        public String getPrevious() {
            return previous;
        }

        public void setPrevious(String previous) {
            this.previous = previous;
        }

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }

        public String getWalle() {
            return walle;
        }

        public void setWalle(String walle) {
            this.walle = walle;
        }

        public String getWalls() {
            return walls;
        }

        public void setWalls(String walls) {
            this.walls = walls;
        }
    }


}
