package cn.http.rdata;

/**
 * Created by lan on 2017/3/31.
 */

public class RUserRegister extends RData {

    /**
     * info : {"identitycheck":"0","hasic":0,"foregift":0}
     */

    private InfoBean info;

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public static class InfoBean {
        /**
         * identitycheck : 0
         * hasic : 0
         * foregift : 0.0
         */

        private String identitycheck;
        private String hasic;
        private String foregift;

        public String getIdentitycheck() {
            return identitycheck;
        }

        public void setIdentitycheck(String identitycheck) {
            this.identitycheck = identitycheck;
        }

        public String getHasic() {
            return hasic;
        }

        public void setHasic(String hasic) {
            this.hasic = hasic;
        }

        public String getForegift() {
            return foregift;
        }

        public void setForegift(String foregift) {
            this.foregift = foregift;
        }
    }
}

