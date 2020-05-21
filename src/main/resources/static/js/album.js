Vue.component('vue-gallery', {
    // 保存数据
    props: ['photos'],
    data: function () {
        return {
            activePhoto: null
        }
    },
    template: `
    <div class="vueGallery">
        <div class="activePhoto" :style="'background-image: url('+photos[activePhoto]+'); background-repeat: no-repeat; '">
            <button type="button" aria-label="Previous Photo" class="previous" @click="previousPhoto()">
                <i class="fas fa-chevron-circle-left"></i>
            </button>
            <button type="button" aria-label="Next Photo" class="next" @click="nextPhoto()">
                <i class="fas fa-chevron-circle-right"></i>
            </button>
        </div>
        <div class="thumbnails">
            <div
                v-for="(photo, index) in photos"
                :src="photo"
                :key="index"
                @click="changePhoto(index)"
                :class="{'active': activePhoto == index}" :style="'background-image: url('+photo+');'">
            </div>
        </div>
    </div>
    `,
    mounted() {
        this.changePhoto(0);
        document.addEventListener("keydown", (event) => {
            if (event.which == 37) {
                this.previousPhoto();
            }
            if (event.which == 39) {
                this.nextPhoto();
            }
        })
    },
    methods: {
        changePhoto(index) {
            this.activePhoto = index;
        },
        nextPhoto() {
            this.changePhoto(this.activePhoto + 1 < this.photos.length ? this.activePhoto + 1 : 0);
        },
        previousPhoto() {
            this.changePhoto(this.activePhoto - 1 >= 0 ? this.activePhoto - 1 : this.photos.length - 1);
        }
    }
});

Vue.component('v-tag', {
    props: ['tags'],
    template: `
    <div>
        <div class="tags-wrap" v-for="(value, key) in tags" :key="key">
            <div class="tags" translate="tags" :style="{backgroundColor: bgc[getBgcNo()]}">
                <span class="content" @click="getCate(key)">{{cateMap[key]}}类</span>
            </div>
            <br>
        
            <div class="tags" translate="tags" :style="{backgroundColor: bgc[getBgcNo()]}"
                v-for="(item, index) in value" :key="index">
                <span class="content" @click="getPhotosByTag(key, item)">{{item}}</span>
            </div>
        
        </div>
    </div>
    `,
    data: function () {
        return {
            "bgc": ['#0998d2', '#e0780e', '#332adb', '#dc683e', '#e37b0e', '#19b0e0', '#e961b4', '#22c7be', '#dc3352', '#98e269'],
            "cateMap": {"expression": "表情", "scene": "场景"},
        }
    },
    methods: {
        getBgcNo: function() {
            return Math.ceil(Math.random() * 10) - 1;
        },
        getPhotosByTag(cate, tag) {
            customEvent.$emit('getPhotosByCateAndLabel', cate, tag);
        },
        getCate(cate) {
            customEvent.$emit('getPhotosByCate', cate);
        }
    }
});

let customEvent = new Vue();

let tagIns = new Vue({
    el: "#my-custom-tags",
    data() {
        return {
            tags: {}
        }
    },
    beforeMount() {
        this.getTags();
    },
    mounted() {
        customEvent.$on('refreshTag', success => {
            this.getTags();
        })
    },
    methods: {
        getBgcNo: function () {
            return Math.ceil(Math.random() * 10) - 1;
        },

        getTags: async function(){
            let res = await axios.get("/album/v1/allCates");
            this.tags= res.data;
            console.log(this.tags);
        }
    }
});

let albumIns = new Vue({
    el: "#app",
    data() {
        return {
            photos: []
        }
    },
    created() {
        this.getData();
    },
    mounted() {
        customEvent.$on('uploadSuccess', success => {
            this.getData();
        })
        customEvent.$on('getPhotosByCateAndLabel', (cate, tag) => {
            this.getPhotosByCateAndLabel(cate, tag)
        })
        customEvent.$on('getPhotosByCate', (cate) => {
            this.getPhotosByCate(cate);
        })
    },
    methods: {
        getData: async function() {
            let res = await axios.get("/album/v1/list");
            this.photos = res.data;
            console.log(this.photos);
        },
        getPhotosByCateAndLabel: async function(cate, tag) {
            let url = `/album/v1/getPhotoByCateAndLabel?tag=${tag}&cate=${cate}`;
            let res = await axios.get(url);
            this.photos = res.data;
            console.log(this.photos);
        },
        getPhotosByCate: async function(cate) {
            let url = `/album/v1/getPhotoByCate?cate=${cate}`;
            let res = await axios.get(url);
            this.photos = res.data;
            console.log(this.photos);
        }
    }
});

let uploadIns = new Vue({
   el: "#upload",
   methods: {
       uploadSuccess: function (response, file, fileList) {
            customEvent.$emit('uploadSuccess', true);
            customEvent.$emit('refreshTag', true);
       }
   }
});

let vm = new Vue({
   el: '#appEvent',
   component: {
       'upload': uploadIns,
       'album': albumIns,
       'custom-tags': tagIns,
   }
});