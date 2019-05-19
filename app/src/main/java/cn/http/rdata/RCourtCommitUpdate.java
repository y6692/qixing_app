package cn.http.rdata;

/**
 * Created by lan on 2017-08-24.
 */

public class RCourtCommitUpdate extends RData {

    /**
     * info : {"oldkey":"ti7onSim9pleBLat","newkey":"UEnTgBWfDjSqsgA8repqQBGgZ2spmCaHMrICeQOkI5lgUtbOLy81RI7IUlAz7NNIxFanaJrTL1xWL6na","index":154}
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
         * oldkey : ti7onSim9pleBLat
         * newkey : UEnTgBWfDjSqsgA8repqQBGgZ2spmCaHMrICeQOkI5lgUtbOLy81RI7IUlAz7NNIxFanaJrTL1xWL6na
         * index : 154
         */

        private String oldkey;
        private String newkey;
        private int index;

        public String getOldkey() {
            return oldkey;
        }

        public void setOldkey(String oldkey) {
            this.oldkey = oldkey;
        }

        public String getNewkey() {
            return newkey;
        }

        public void setNewkey(String newkey) {
            this.newkey = newkey;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
}
