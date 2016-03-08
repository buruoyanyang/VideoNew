package com.example.biezhi.videonew.DataModel;

import java.util.List;

/**
 * Created by xiaofeng on 16/3/8.
 */
public class VideoInfoModel {

    /**
     * actors : []
     * adult : false
     * brief :
     * cateId : 3
     * content : [{"id":32,"name":"原创","shortName":"爱奇艺会员","domain":"","seq":"01","status":1,"episodeNum":1,"canDownload":true}]
     * cover : http://m.qiyipic.com/image/20160113/58/be/a_100020284_m_601_180_236.jpg
     * dayPlayNum : 0
     * director : 王涛
     * districts : []
     * duration : 0
     * id : 319204
     * intro :
     分类：喜剧
     地区：香港
     导演：王涛
     主演：吴刚,李燕燕,谢瑜 刘菁菁
     时长：
     简介：银行职员林少鸿在追还顾客徐小姐遗落的现款时被巡警误伤。徐小姐由此在感激之余默默爱上了他。林少鸿本有女友程思雅，谁知其父母定要招林入赘，而林少鸿的母亲坚决不依。结果，双方未成亲家，先结冤家。林母要儿子娶徐小姐。当徐了解到林和程的关系后，主动成全了他们，但林母、程母分歧依旧。林、程在外出商议对策时被劫徒打昏住进医院。双方亲家误以为他们殉情自杀，悔恨当初。林少鸿与程思雅终结良缘，林、程两家终成亲家。…

     * kinds : [{"id":7,"name":"喜剧"}]
     * name : 欢天喜地对亲家未删减版
     * playNum : 281386
     * rating : 0
     * update : 48集全
     * updateUrl :
     * url :
     * version : 203498101
     * year : 2015
     * type : 0
     */

    private boolean adult;
    private String brief;
    private int cateId;
    private String cover;
    private int dayPlayNum;
    private String director;
    private int duration;
    private int id;
    private String intro;
    private String name;
    private int playNum;
    private int rating;
    private String update;
    private String updateUrl;
    private String url;
    private String version;
    private String year;
    private int type;
    private List<?> actors;
    /**
     * id : 32
     * name : 原创
     * shortName : 爱奇艺会员
     * domain :
     * seq : 01
     * status : 1
     * episodeNum : 1
     * canDownload : true
     */

    private List<ContentEntity> content;
    private List<?> districts;
    /**
     * id : 7
     * name : 喜剧
     */

    private List<KindsEntity> kinds;

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public void setCateId(int cateId) {
        this.cateId = cateId;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setDayPlayNum(int dayPlayNum) {
        this.dayPlayNum = dayPlayNum;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlayNum(int playNum) {
        this.playNum = playNum;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setActors(List<?> actors) {
        this.actors = actors;
    }

    public void setContent(List<ContentEntity> content) {
        this.content = content;
    }

    public void setDistricts(List<?> districts) {
        this.districts = districts;
    }

    public void setKinds(List<KindsEntity> kinds) {
        this.kinds = kinds;
    }

    public boolean isAdult() {
        return adult;
    }

    public String getBrief() {
        return brief;
    }

    public int getCateId() {
        return cateId;
    }

    public String getCover() {
        return cover;
    }

    public int getDayPlayNum() {
        return dayPlayNum;
    }

    public String getDirector() {
        return director;
    }

    public int getDuration() {
        return duration;
    }

    public int getId() {
        return id;
    }

    public String getIntro() {
        return intro;
    }

    public String getName() {
        return name;
    }

    public int getPlayNum() {
        return playNum;
    }

    public int getRating() {
        return rating;
    }

    public String getUpdate() {
        return update;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public String getYear() {
        return year;
    }

    public int getType() {
        return type;
    }

    public List<?> getActors() {
        return actors;
    }

    public List<ContentEntity> getContent() {
        return content;
    }

    public List<?> getDistricts() {
        return districts;
    }

    public List<KindsEntity> getKinds() {
        return kinds;
    }

    public static class ContentEntity {
        private int id;
        private String name;
        private String shortName;
        private String domain;
        private String seq;
        private int status;
        private int episodeNum;
        private boolean canDownload;

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public void setSeq(String seq) {
            this.seq = seq;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public void setEpisodeNum(int episodeNum) {
            this.episodeNum = episodeNum;
        }

        public void setCanDownload(boolean canDownload) {
            this.canDownload = canDownload;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getShortName() {
            return shortName;
        }

        public String getDomain() {
            return domain;
        }

        public String getSeq() {
            return seq;
        }

        public int getStatus() {
            return status;
        }

        public int getEpisodeNum() {
            return episodeNum;
        }

        public boolean isCanDownload() {
            return canDownload;
        }
    }

    public static class KindsEntity {
        private int id;
        private String name;

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
