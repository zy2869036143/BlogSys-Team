<template>
  <div style="width: 100%">
    <el-card>
      <div>
          <h2 style="margin-bottom: 8px"> {{ blog.title }}
            <el-tooltip placement="top" effect="dark" content="重新编辑博客">
              <el-button type="primary" icon="el-icon-edit" plain v-if="ownBlog"
                         size="mini" circle
                         @click="handleEdit()" style="margin-top: 10px">
              </el-button>
            </el-tooltip>
          </h2>

        <h3 style="color: gainsboro;font-size: 10px;margin-top: 10px;margin-bottom: 10px">
          <el-button type="text" style="color: gainsboro" @click="pushOwn($event)">作者:{{blog.username}}</el-button>

          <el-tooltip placement="top" effect="dark" content="关注" v-if="!ifSub && !ownBlog">
            <el-button type="warning" icon="el-icon-star-off" plain
                       size="mini" round
                       @click="onSub($event)" style="margin-top: 10px;margin-left: 10px">关注该作者
            </el-button>
          </el-tooltip>
          <el-tooltip placement="top" effect="dark" content="取消关注" v-if="ifSub && !ownBlog">
            <el-button type="info" icon="el-icon-star-off" plain
                       size="mini" round
                       @click="onSub($event)" style="margin-top: 10px;margin-left: 10px">已关注
            </el-button>
          </el-tooltip>
        </h3>

      </div>
      <div style="margin-top: 10px">
        <el-tooltip v-for="(tag,index) in tags" :key="index" :content="tag.id" placement="bottom" effect="dark" v-if="tag.ifChosen">
          <el-button  :icon="tag.icon" circle type="success" plain size="mini"></el-button>
        </el-tooltip>
      </div>
      <el-divider></el-divider>
      <div class="markdown-body" v-html="content"></div>
      <el-divider></el-divider>
      <div style="float: right;font-size: 10px;color: grey;margin-top: 0">发布于：{{blog.created}}</div>
      <el-button circle v-if="!ifLike" icon="el-icon-icon" @click.native="like($event)" size="mini">{{blog.praise}}</el-button>
      <el-button circle v-if="ifLike" type="primary" icon="el-icon-icon-copy" @click.native="like($event)" size="mini">{{blog.praise}}</el-button>
      <el-button circle v-if="!ifFavourite" icon="el-icon-shoucang" @click.native="favourite($event)" size="mini">{{blog.favorite}}</el-button>
      <el-button circle v-if="ifFavourite" type="warning" icon="el-icon-shoucang" @click.native="favourite($event)" size="mini">{{blog.favorite}}</el-button>
      <el-button circle v-if="!ifComment"  icon="el-icon-pinglun" @click.native="comment($event)" size="mini"></el-button>
      <el-button circle v-if="ifComment" type="info" icon="el-icon-pinglun" @click.native="comment($event)" size="mini"></el-button>
      <div v-if="ifComment">
        <h3>发表你的评论：</h3>
        <el-input type="textarea" v-model="yourComment"></el-input>
        <el-button size="mini" style="float: right;margin-top: 10px;margin-bottom: 10px" type="primary" @click="onComment(0,yourComment)">确定</el-button>
      </div>


    </el-card>
    <el-card style="margin-top: 20px" class="comments-container">
<!--      <h1>评论：</h1>-->
<!--      <el-card style="margin-bottom: 10px" shadow="hover" v-for="comment in comments" :key="comment.id">-->
<!--        <h5>{{comment.who}}</h5>-->
<!--        <h3>-->
<!--          {{comment.content}}-->
<!--        </h3>-->
<!--      </el-card>-->
      <div >
        <h3>评论卡：</h3>
        <ul v-if="comments.length>0" id="comments-list" class="comments-list">
          <li v-for="(comment) in comments" >
            <div class="comment-main-level">
              <!-- Avatar -->
<!--              <div class="comment-avatar"></div>-->
              <!-- Contenedor del Comentario -->
              <div class="comment-box">
                <div class="comment-head">
                  <h6 class="comment-name by-author" v-if="comment[0].userId === blog.userId">{{comment[0].username}}</h6>
                  <h6 v-else class="comment-name">{{comment[0].username}}</h6>
                  <div style="float: right;font-size: 10px;color: grey">
                    {{comment[0].time}}
                  </div>
                </div>
                <div class="comment-content">
                 {{comment[0].content}}
                </div>
                <el-button type="text" v-if="!comment[0].ifReply"  icon="el-icon-pinglun" @click.native="inComment($event,comment[0])" size="mini" style="margin: 10px;color: black">回复</el-button>
                <el-button type="text" v-if="comment[0].ifReply"  icon="el-icon-pinglun" @click.native="inComment($event,comment[0])" size="mini" style="margin: 13px">回复</el-button>

                <el-button type="text" icon="el-icon-delete" @click.native="deleteComment(comment[0].id)" size="mini" style="margin: 10px;color: red;" v-if="comment[0].userId===user.id">删除</el-button>
                <div v-if="comment[0].ifReply" style="margin-left: 13px;margin-right: 13px">
                  <el-input type="textarea" v-model="reply"></el-input>
                  <el-button size="mini" style="float: right;margin-top: 10px;margin-bottom: 10px" type="primary" @click="onComment(comment[0].id,reply)">确定</el-button>
                </div>
              </div>
            </div>
            <!-- Respuestas de los comentarios -->
            <ul class="comments-list reply-list">
              <li v-for="i in comment.length-1" :key="i">
                <!-- Avatar -->
                <!-- Contenedor del Comentario -->
                <div class="comment-box">
                  <div class="comment-head">
                      <h6 class="comment-name by-author" v-if="comment[i].userId === blog.userId">{{comment[i].username}}</h6>
                      <h6 v-else class="comment-name">{{comment[i].username}}</h6>
                      <h6 v-if="comment[i].followUsername&&comment[i].follow !== comment[0].id"  style="color: #8c939d;font-size: 10px;float: left;margin-right: 10px;margin-top: 34px">回复{{ comment[i].followUsername }}</h6>
                    <div style="float: right;font-size: 10px;color: grey">
                      {{comment[i].time}}
                    </div>
                  </div>

                  <div class="comment-content">
                    {{comment[i].content}}
                  </div>
                  <el-button type="text" v-if="!comment[i].ifReply"  icon="el-icon-pinglun" @click.native="inComment($event,comment[i])" size="mini" style="margin: 10px;color: black">回复</el-button>
                  <el-button type="text" v-if="comment[i].ifReply"  icon="el-icon-pinglun" @click.native="inComment($event,comment[i])" size="mini" style="margin: 13px">回复</el-button>

                  <el-button type="text" icon="el-icon-delete" @click.native="deleteComment(comment[i].id)" size="mini" style="margin: 10px;color: red;" v-if="comment[i].userId===user.id">删除</el-button>
                  <div v-if="comment[i].ifReply" style="margin-left: 13px;margin-right: 13px">
                    <el-input type="textarea" v-model="reply"></el-input>
                    <el-button size="mini" style="float: right;margin-top: 10px;margin-bottom: 10px" type="primary" @click="onComment(comment[i].id,reply)">确定</el-button>
                  </div>
                </div>
              </li>

            </ul>
          </li>

        </ul>

        <div v-else>
          <el-empty description="暂无评论"></el-empty>
        </div>
      </div>

    </el-card>

  </div>
</template>

<script>

import request from "../util/request";

export default {
  data() {
    return {
      ifSub:false,
      tags:[
        {ifChosen:false,
          id:"python",
          icon:"el-icon-python"
        },
        {
          id:"c/c++",
          icon:"el-icon-cyuyan",
          ifChosen:false
        },
        {
          id:"java",
          icon:"el-icon-java",
          ifChosen:false
        },
        {
          id:"dart",
          icon:"el-icon-dart",
          ifChosen:false
        },
        {ifChosen:false,
          id:"javaScript",
          icon:"el-icon-js"
        },
        {ifChosen:false,
          id:"swift",
          icon:"el-icon-swift"
        },
        {ifChosen:false,
          id:"c#",
          icon:"el-icon-Cyuyan"
        },
        {ifChosen:false,
          id:"其他",
          icon:"el-icon-more-outline"
        },
      ],
      content:'',
      ifLogin:false,
      reply:'',
      ifComment:false,
      ifLike:false,
      ifFavourite:false,
      yourComment:'',
      comments:[
        [
        {
          id: '6',
          userId: '6',
          blogId:'1',
          content:"23131232",
          follow:'0',
          username:'ccc',
        }
      ],
        [
          {
            id: '6',
            userId: '2',
            blogId:'1',
            content:"23131vcvcvcv232",
            follow:'0',
          },
          {
            id: '6',
            userId: '4',
            blogId:'1',
            content:"adsdsdasds",
            follow:'2',
          },
          {
            id: '6',
            userId: '4',
            blogId:'1',
            content:"adsdsdweqweqeqwewqeasds",
            follow:'2',
          },
        ]],
      blog: {
        id: "1",
        title: "111111",
        content: "2222222222"
      },
      user:'',
      ownBlog: true,
      labelT:'',
    }
  },
  methods:{
    pushOwn(e){
      e.stopPropagation()
      if(this.ownBlog){
        this.$router.push({
          path:`/personPage/${encodeURIComponent(JSON.stringify(this.user))}`,
        })
      }else {
        console.log(this.blog.userId)
        request.get("http://localhost:8081/userinfo/getinfo?id="+this.blog.userId).then(res=>{
          if(res.code === 200){
            let blogUser = res.data
            console.log(blogUser)
            this.$router.push({path:`/othersPage/${encodeURIComponent(JSON.stringify(blogUser))}`})
          }
        })
      }
    },
    onSub(e){
      e.stopPropagation();
      if(!this.ifLogin){
        this.$message({
          type:'warning',
          message:'请先登录'
        })
        let target = e.target;
        if(target.nodeName === "I"||target.nodeName === "svg"){
          target = e.target.parentNode;
        }
        target.blur();
        return
      }
      if(!this.ifSub){

        let data = {
          id: this.user.id,
          sid: this.blog.userId,
        }
        this.ifSub = !this.ifSub
        console.log(data)
        request.post("http://localhost:8081/subscribe/addsubscribe",data).then(res=>{
          console.log(res.code)
          if(res.code === 200){
          }
        })
      }else {
        let data = {
          id: this.user.id,
          sid: this.blog.userId,
        }
        this.ifSub = !this.ifSub
        console.log(data)
        request.post("http://localhost:8081/subscribe/delsubscribe",data).then(res=>{
          console.log(res.code)
          if(res.code === 200){
          }
        })
      }
      let target = e.target;
      if(target.nodeName === "I"||target.nodeName === "svg"){
        target = e.target.parentNode;
      }
      target.blur();
    },
    handleEdit(){
      let editData = {
        user:this.user,
        blogId:this.blog.id,
      }
      this.$router.push({
        path:`/editBlogs/${encodeURIComponent(JSON.stringify(editData))}`,
      })
    },
    like(e) {
      e.stopPropagation();
      if(!this.ifLogin){
        this.$message({
          type:'warning',
          message:'请先登录'
        })
        let target = e.target;
        if(target.nodeName === "I"||target.nodeName === "svg"){
          target = e.target.parentNode;
        }
        target.blur();
        return
      }
      if(!this.ifLike){

        let data = {
          pid: this.user.id,
          pblogid: this.blog.id,
        }
        this.ifLike = !this.ifLike
        console.log(data)
        request.post("http://localhost:8081/praise/addpraiseinfo",data).then(res=>{
          console.log(res.code)
          if(res.code === 200){
            let temp = this.blog;
            temp.created = '';
            temp.label = this.labelT
            console.log(temp)
            request.post("http://localhost:8081/blog/addpraisenum",temp).then(res1=>{
              if(res1.code === 200){
                this.blog.praise = this.blog.praise+1
              }
            })

          }
        })
      }
      else{
        let data = {
          pid: this.user.id,
          pblogid: this.blog.id,
        }
        this.ifLike = !this.ifLike
        console.log(data)
        request.post("http://localhost:8081/praise/delpraiseinfo",data).then(res=>{
          console.log(res.code)
          if(res.code === 200){
            let temp = this.blog;
            temp.created = '';
            temp.label = this.labelT
            console.log(temp)
            request.post("http://localhost:8081/blog/delpraisenum",temp).then(res1=>{
              if(res1.code === 200){
                this.blog.praise = this.blog.praise-1
              }
            })

          }
        })
      }
      this.$forceUpdate()
      let target = e.target;
      if(target.nodeName === "I"||target.nodeName === "svg"){
        target = e.target.parentNode;
      }
      target.blur();
    },

    favourite(e) {
      e.stopPropagation();
      if(!this.ifLogin){
        this.$message({
          type:'warning',
          message:'请先登录'
        })
        let target = e.target;
        if(target.nodeName === "I"||target.nodeName === "svg"){
          target = e.target.parentNode;
        }
        target.blur();
        return
      }
      if(!this.ifFavourite){

        let data = {
          fid: this.user.id,
          fblogid: this.blog.id,
        }
        this.ifFavourite = !this.ifFavourite
        console.log(data)
        request.post("http://localhost:8081/favorite/addfavoriteinfo",data).then(res=>{
          console.log(res.code)
          if(res.code === 200){
            let temp = this.blog;
            temp.created = '';
            temp.label = this.labelT
            console.log(temp)
            request.post("http://localhost:8081/blog/addfavoritenum",temp).then(res1=>{
              if(res1.code === 200){
                this.blog.favorite = this.blog.favorite+1
              }
            })

          }
        })
      }
      else{
        let data = {
          fid: this.user.id,
          fblogid: this.blog.id,
        }
        this.ifFavourite = !this.ifFavourite
        console.log(data)
        request.post("http://localhost:8081/favorite/delfavoriteinfo",data).then(res=>{
          console.log(res.code)
          if(res.code === 200){
            let temp = this.blog;
            temp.created = '';
            temp.label = this.labelT
            request.post("http://localhost:8081/blog/delfavoritenum",temp).then(res1=>{
              if(res1.code === 200){
                this.blog.favorite = this.blog.favorite-1
              }
            })

          }
        })
      }

      this.$forceUpdate()
      let target = e.target;
      if(target.nodeName === "I"||target.nodeName === "svg"){
        target = e.target.parentNode;
      }
      target.blur();
    },
    comment(e) {
      e.stopPropagation();
      if(!this.ifLogin){
        this.$message({
          type:'warning',
          message:'请先登录'
        })
        let target = e.target;
        if(target.nodeName === "I"||target.nodeName === "svg"){
          target = e.target.parentNode;
        }
        target.blur();
        return
      }
      this.ifComment = !this.ifComment
      let target = e.target;
      if(target.nodeName === "I"||target.nodeName === "svg"){
        target = e.target.parentNode;
      }
      target.blur();
    },
    inComment(e,comment1) {
      // this.ifInComment = !this.ifInComment
      if(!this.ifLogin){
        this.$message({
          type:'warning',
          message:'请先登录'
        })
        let target = e.target;
        if(target.nodeName === "I"||target.nodeName === "svg"){
          target = e.target.parentNode;
        }
        target.blur();
        return
      }
      comment1.ifReply = !comment1.ifReply;
      this.$forceUpdate()
      let target = e.target;
      if(target.nodeName === "I"||target.nodeName === "svg"){
        target = e.target.parentNode;
      }
      target.blur();
    },
    onComment(follow,yourComment){
      let commentData = {
        userId: this.user.id,
        blogId: this.blog.id,
        content: yourComment,
        follow: follow,
      }
      request.post("http://localhost:8081/comment/save",commentData).then(res=>{
        if(res.code === 200){
          this.$message({
            type: "success",
            message: res.msg,
          })
          this.yourComment = ''
          this.reply = ''
          request.post("http://localhost:8081/comment/get?id="+this.blog.id).then(res1=>{
            if(res1.code === 200){
              this.comments = res1.data;
              for(let i = 0;i<this.comments.length;i++){
                for(let j = 0;j<this.comments[i].length;j++){
                  this.comments[i][j].ifReply = false;
                }
              }
            }
          })
        }
      })
    },
    deleteComment(commentId){
      let deleteData = {
        userId: this.user.id,
        commentId: commentId,
      }
      console.log(deleteData)
      request.post("http://localhost:8081/comment/delete",deleteData).then(res=>{
        if(res.code===200){
          this.$message({
            type:"success",
            message:"删除评论成功"
          })
          request.post("http://localhost:8081/comment/get?id="+this.blog.id).then(res1=>{
            if(res1.code === 200){
              this.comments = res1.data;
              for(let i = 0;i<this.comments.length;i++){
                for(let j = 0;j<this.comments[i].length;j++){
                  this.comments[i][j].ifReply = false;
                }
              }
              console.log(this.comments)
            }
          })
        }else {
          this.$message({
            type:"error",
            message:res.msg,
          })
        }
      })

    }
  },
  created() {
    let data =  JSON.parse(decodeURIComponent(this.$route.params.data))
    let blogId = data.blogId
    // this.user = data.user
    if(localStorage.getItem("user")!==""&&localStorage.getItem("user")){
      this.user = JSON.parse(decodeURIComponent(localStorage.getItem("user")))
      this.ifLogin = true;
      console.log(this.ifLogin)
    }else {
      if(!this.$route.params.user){
        this.ifLogin = false;
      }else{
        this.ifLogin = true
        this.user = JSON.parse(decodeURIComponent(this.$route.params.user))
        localStorage.setItem('user',this.$route.params.user);
      }
    }
    // if(this.user){
    //   this.ifLogin = true
    // }else {
    //   this.ifLogin = false;
    // }
    let url = 'http://localhost:8081/blog/'+blogId
    request.get(url).then(res=>{
      this.blog = res.data
      this.blog.username = res.msg
      this.labelT = this.blog.label
      if(this.blog.userId === this.user.id){
        this.ownBlog = true;
      }else{
        this.ownBlog = false;
      }
      this.blog.label = JSON.parse(this.blog.label)
      if(this.blog.label){
        for(let i = 0;i<this.tags.length;i++){
          for(let j = 0;j<this.blog.label.length;j++){
            if(this.blog.label[j] === this.tags[i].id){
              this.tags[i].ifChosen = true;
            }
          }
        }
      }

      console.log(this.tags)
      this.$forceUpdate()

      var MardownIt = require("markdown-it")
      var md = new MardownIt()

      var result = md.render(this.blog.content)
      this.content = result
      console.log(this.blog.content)
      // this.ownBlog = (blog.userId === this.$store.getters.getUser.id)
      request.get("http://localhost:8081/praise/getpraiseinfo?userid="+this.user.id).then(res1=>{
        if(res1.code === 200){
          let praise = res1.data
            for(let j = 0;j<praise.length;j++){
              if(praise[j].pblogid === this.blog.id){
                this.ifLike = true;
                break;
              }else {
                this.ifLike = false;
              }
            }

          this.$forceUpdate()
        }
      })

      request.post("http://localhost:8081/favorite/getfavoriteinfo?userid="+this.user.id).then(res1=>{
        if(res1.code === 200){
          let favorite = res1.data
          console.log(favorite)
            for(let j = 0;j<favorite.length;j++){
              if(favorite[j].fblogid === this.blog.id){
                this.ifFavourite= true;
                break;
              }else {
                this.ifFavourite = false;
              }
            }
          this.$forceUpdate()
        }
      })

      let commentData = {
        id: blogId,
      }

      request.post("http://localhost:8081/comment/get?id="+blogId).then(res1=>{
        if(res1.code === 200){
          this.comments = res1.data;
          for(let i = 0;i<this.comments.length;i++){
            for(let j = 0;j<this.comments[i].length;j++){
              this.comments[i][j].ifReply = false;
            }
          }
          console.log(this.comments)
        }
      })

      request.post("http://localhost:8081/subscribe/getsubscribe?id="+this.user.id).then(res=>{
        if(res.code === 200){
          let sub = res.data
          console.log(222)
          console.log(sub)
          for(let i = 0;i<sub.length;i++){
            if(sub[i].id === this.blog.userId){
              this.ifSub = true;
              break;
            }
          }
          this.$forceUpdate()
        }
      })
    })
  }
}
</script>

<style scoped>
.mblog {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  width: 100%;
  padding-left: 15px;
  padding-right: 15px;
}

.comments-container {
  margin: 60px auto 15px;
  width: 768px;
}

.comments-container h1 {
  font-size: 36px;
  color: #283035;
  font-weight: 400;
}

.comments-container h1 a {
  font-size: 18px;
  font-weight: 700;
}

.comments-list {
  margin-top: 30px;
  position: relative;
}

.comments-list:before {
  content: '';
  width: 2px;
  height: 100%;
  background: #c7cacb;
  position: absolute;
  left: 32px;
  top: 0;
}

.comments-list:after {
  content: '';
  position: absolute;
  background: #c7cacb;
  bottom: 0;
  left: 27px;
  width: 7px;
  height: 7px;
  border: 3px solid #dee1e3;
  -webkit-border-radius: 50%;
  -moz-border-radius: 50%;
  border-radius: 50%;
}

.reply-list:before, .reply-list:after {display: none;}
.reply-list li:before {
  content: '';
  width: 60px;
  height: 2px;
  background: #c7cacb;
  position: absolute;
  top: 25px;
  left: -55px;
}


.comments-list li {
  margin-bottom: 15px;
  display: block;
  position: relative;
}

.comments-list li:after {
  content: '';
  display: block;
  clear: both;
  height: 0;
  width: 0;
}

.reply-list {
  padding-left: 88px;
  clear: both;
  margin-top: 15px;
}
.comments-list .comment-avatar {
  width: 65px;
  height: 65px;
  position: relative;
  float: left;
  border: 3px solid #FFF;
  overflow: hidden;
}

.comments-list .comment-avatar img {
  width: 100%;
  height: 100%;
}

.reply-list .comment-avatar {
  width: 50px;
  height: 50px;
}

.comment-main-level:after {
  content: '';
  width: 0;
  height: 0;
  display: block;
  clear: both;
}
.comments-list .comment-box {
  width: 680px;
  float: right;
  position: relative;
  -webkit-box-shadow: 0 1px 1px rgba(0,0,0,0.15);
  -moz-box-shadow: 0 1px 1px rgba(0,0,0,0.15);
  box-shadow: 0 1px 1px rgba(0,0,0,0.15);
}

.comments-list .comment-box:before, .comments-list .comment-box:after {
  content: '';
  height: 0;
  width: 0;
  position: absolute;
  display: block;
  border-width: 10px 12px 10px 0;
  border-style: solid;
  border-color: transparent #FCFCFC;
  top: 8px;
  left: -11px;
}

.comments-list .comment-box:before {
  border-width: 11px 13px 11px 0;
  border-color: transparent rgba(0,0,0,0.05);
  left: -12px;
}

.reply-list .comment-box {
  width: 610px;
}
.comment-box .comment-head {
  background: #FCFCFC;
  padding: 10px 12px;
  border-bottom: 1px solid #E5E5E5;
  overflow: hidden;
  -webkit-border-radius: 4px 4px 0 0;
  -moz-border-radius: 4px 4px 0 0;
  border-radius: 4px 4px 0 0;
}

.comment-box .comment-head i {
  float: right;
  margin-left: 14px;
  position: relative;
  top: 2px;
  color: #A6A6A6;
  cursor: pointer;
  -webkit-transition: color 0.3s ease;
  -o-transition: color 0.3s ease;
  transition: color 0.3s ease;
}

.comment-box .comment-head i:hover {
  color: #03658c;
}

.comment-box .comment-name {
  color: #283035;
  font-size: 14px;
  font-weight: 700;
  float: left;
  margin-right: 10px;
}

.comment-box .comment-name a {
  color: #283035;
}

.comment-box .comment-head span {
  float: left;
  color: #999;
  font-size: 13px;
  position: relative;
  top: 1px;
}

.comment-box .comment-content {
  background: #FFF;
  padding: 12px;
  font-size: 15px;
  color: #595959;
  -webkit-border-radius: 0 0 4px 4px;
  -moz-border-radius: 0 0 4px 4px;
  border-radius: 0 0 4px 4px;
}

.comment-box .comment-name.by-author, .comment-box .comment-name.by-author a {color: #03658c;}
.comment-box .comment-name.by-author:after {
  content: '作者';
  background: #03658c;
  color: #FFF;
  font-size: 12px;
  padding: 3px 5px;
  font-weight: 700;
  margin-left: 10px;
  -webkit-border-radius: 3px;
  -moz-border-radius: 3px;
  border-radius: 3px;
}

/** =====================
 * Responsive
 ========================*/
@media only screen and (max-width: 766px) {
  .comments-container {
    width: 480px;
  }

  .comments-list .comment-box {
    width: 390px;
  }

  .reply-list .comment-box {
    width: 320px;
  }
}


a {
  color: #03658c;
  text-decoration: none;
}

ul {
  list-style-type: none;
}

body {
  font-family: 'Roboto', Arial, Helvetica, Sans-serif, Verdana,serif;
  background: #dee1e3;
}

</style>
