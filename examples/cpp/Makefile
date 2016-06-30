CXXFLAGS=-g

bin = plu_client
objs = main.o barrage_client.o data_def.o

$(bin): $(objs)
	g++ -o $(bin) $(objs) -lpthread

.phony: clean

clean:
	rm $(objs) $(bin)
