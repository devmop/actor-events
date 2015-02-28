package users

import "container/list"
import "testing"
import "github.com/devmop/event-stream/events"

func TestReceivesRegistration(t *testing.T) {
	e, r := setup()

	m := events.NewMessage(RegistrationRequest{email: "email@example.com", password: "password"})

	e.Send(m)

	e.Terminate()

	envelope := findRegistration(m.Id(), r.messages)

	if envelope == nil {
		t.Error("Should have received registration with same id")
	}

	result := envelope.Content.(Registration)

	if result.email != "email@example.com" {
		t.Errorf("Email should be email@example.com but was %s", result.email)
	}

	if result.password != "password" {
		t.Errorf("Password should be password but was %s", result.password)
	}
}

func findRegistration(id int64, messages *list.List) *events.Message {
	for it := messages.Front(); it.Value != nil; it = it.Next() {
		m := it.Value.(events.Message)
		if m.Id() == id {
			if _, ok := m.Content.(Registration); ok {
				return &m
			}
		}
	}
	return nil
}

func setup() (events.Stream, recorder) {
	e := events.SyncStream()

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

func (e recorder) Terminate() {
}
