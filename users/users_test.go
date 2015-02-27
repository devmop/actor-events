package users

import "container/list"
import "testing"
import "github.com/devmop/event-stream/events"

func TestReceivesRegistration(t *testing.T) {
	e, r := setup()

	m := events.NewMessage(RegistrationRequest{email: "email@example.com", password: "password"})

	e.Send(m)

	envelope := r.messages.Back().Value.(events.Message)
	result := envelope.Content.(Registration)

	if result.email != "email@example.com" {
		t.Errorf("Email should be email@example.com but was %s", result.email)
	}

	if result.password != "password" {
		t.Errorf("Password should be password but was %s", result.password)
	}

	if m.Id() != envelope.Id() {
		t.Error("Should inherit request id")
	}
}

func setup() (events.Stream, recorder) {
	e := events.NewStream()

	Initialise(e)

	r := recorder{list.New()}
	e.Register(r)

	return e, r
}

type recorder struct {
	messages *list.List
}

func (e recorder) Receive(m events.Message) {
	e.messages.PushFront(m)
}
