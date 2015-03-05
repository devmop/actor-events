package main

import "os"
import "syscall"
import "os/signal"
import "github.com/devmop/event-stream/application"

func main() {
	stream := application.Initialise(application.Users, application.Public)

	ch := make(chan os.Signal)
	signal.Notify(ch, syscall.SIGINT, syscall.SIGTERM)
	<-ch

	stream.Terminate()
}
