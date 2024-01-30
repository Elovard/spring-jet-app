<template>
  <div style="width: 100%; min-height: 100vh; display: inline-flex">
    <div class="messages">
      <div v-for="(message, ind) in messages" :key="ind" :title="JSON.stringify(message)">
        <div class="msg" v-if="message.source === 'AIRPORT'">A</div>
        <div class="msg" v-if="message.source === 'OFFICE' && messages.type === 'ROUTE'">&#9758;</div>
        <div class="msg" v-if="message.source === 'OFFICE' && messages.type === 'STATE'">?</div>
        <div class="msg" v-if="message.source === 'BOARD'">&#9992;</div>
      </div>
    </div>
    <div class="radar">

      <Port v-for="port in ports"
            :key="port.name"
            :port="port"
            :clickCallback="addRoute" />

      <div class="planeArea">
        <Plane v-for="(row, ind) in boards" :key="ind" :plane="row"></Plane>
      </div>

      <div style="padding: 10px; text-align: center; color: #FFF;">
        <span v-for="(route, index) in tempRoute">
          {{route}} <b v-if="index !== tempRoute.length - 1">&rtri;</b>
        </span>
        <button @click="submitRoute" v-if="tempRoute.length > 1" class="roundBtn">&check;</button>
        <button @click="cancelRoute" v-if="tempRoute.length > 0" class="roundBtn">&cross;</button>
      </div>

    </div>
  </div>
</template>

<script>
import axios from 'axios';
import Port from "~/components/Port.vue";
export default {
  components: {Port},
  data() {
    return {
      socket: null,
      ports: [],
      boards: [],
      messages: [],
      tempRoute: []
    }
  },
  mounted() {
    this.defineSocket();
    setInterval(this.wakeSocketUp, 500);
  },
  methods: {

    defineSocket() {
      this.socket = new WebSocket("ws://localhost:8083/websocket")
      this.socket.onopen = () => {
        this.socket.onmessage = (msg, ctx) => {
          let message = JSON.parse(msg.data);
          if (message.source === "AIRPORT") {
            this.setAirPort(message.airport);
          }
          if (message.source === "BOARD" && message.type === "STATE") {
            this.setBoard(message.board);
          }
          this.messages.unshift(message);
          if (this.messages.length > 10) {
            this.messages.splice(10);
          }
        }
      }
    },

    addRoute(port) {
      let last = null;
      if (this.tempRoute.length > 0) {
        last = this.tempRoute[this.tempRoute.length - 1];
      }

      if (last != null && last === port.name) {
        return;
      }
      this.tempRoute.push(port.name);
    },

    submitRoute() {
      axios.post("api/routes/route", this.tempRoute);
      this.tempRoute = []
    },

    cancelRoute() {
      this.tempRoute = [];
    },

    wakeSocketUp() {
      if (this.socket) {
        if (this.ports.length === 0) {
          this.socket.send("update")
        }
      } else {
        this.defineSocket();
      }
    },

    setAirPort(port) {
      let ind = -1;
      this.ports.forEach((row, i) => {
        if (row.name === port.name) {
          ind = i;
        }
      });

      if (ind >= 0) {
        this.ports.splice(ind, 1);
      }
      this.ports.push(port);
    },

    setBoard(board) {
      let existsIndex = -1;
      this.boards.forEach((row, i) => {
        if (row.name === board.name) {
          existsIndex = i;
        }
      });

      if (existsIndex >= 0) {
        this.boards.splice(existsIndex, 1);
      }

      if (!board.busy) {
        return;
      }

      this.boards.push();
    }

  }
}
</script>
<style>
* {
  padding: 0;
  margin: 0;
}

.roundBtn {
  padding: 5px 10px;
  border-radius: 5px;
  border-bottom: white 2px solid;;
  color: #FFF;
  cursor: pointer;
  opacity: .7;
}

.roundBtn:hover {
  opacity: 1;
}

.radar {
  width: 90%;
  position: relative;
  background: transparent url("assets/grass.png");
  min-height: 100vh;
}

.messages {
  padding: 10px;
  width: 10%;
  background: #232323;
}

.planeArea {
  position: absolute;
  right: 0;
  left: 0;
  top: 0;
  bottom: 0;
  pointer-events: none;
}

.msg {
  border-radius: 10px;
  background: #FFF;
  line-height: 40px;
  color: #232323;
  font-size: 30px;
  width: 50px;
  margin: 10px auto;
  text-align: center;
}
</style>
