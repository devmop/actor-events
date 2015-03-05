package public

import "encoding/json"
import "fmt"
import "io/ioutil"
import "time"
import "net/http"
import "github.com/gorilla/mux"
import "github.com/devmop/event-stream/events"
import "github.com/devmop/event-stream/users"

func Initialise(stream events.Stream) {
	r := mux.NewRouter()
	r.HandleFunc("/users",
		func(res http.ResponseWriter, req *http.Request) {
			acceptRegistration(stream, res, req)
		}).Methods("POST")
	go http.ListenAndServe(":4040", r)
}

func acceptRegistration(stream events.Stream, res http.ResponseWriter, req *http.Request) {
	reg := &users.RegistrationRequest{}
	json.NewDecoder(req.Body).Decode(reg)
	ioutil.ReadAll(req.Body)
	req.Body.Close()

	msg := events.NewMessage(reg)
	events.Lambda(stream, func(m events.Message) { returnRegistration(msg.Id(), res, m) })

	stream.Send(msg)
	fmt.Println(time.Now())
	time.Sleep(500 * time.Millisecond)
	res.WriteHeader(http.StatusServiceUnavailable)
}

func returnRegistration(id int64, res http.ResponseWriter, m events.Message) {
	if m.Id() == id {
		reg, ok := m.Content.(*users.Registration)
		if ok {
			res.Header().Add("Content-Type", "application/json")
			res.WriteHeader(http.StatusCreated)
			bytes, _ := json.Marshal(reg)
			res.Write(bytes)
		}

		_, ok = m.Content.(*users.AlreadyRegistered)
		if ok {
			res.WriteHeader(http.StatusConflict)
		}
	}
}
