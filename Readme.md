# RenderSiren

![Naga Siren from Dota 2](https://github.com/Pebaz/RenderSiren/raw/master/res/Naga%20Siren%20Pink.png)

A simple project created for the purposes of showcasing my ability to write
Java code for [American Public University](http://www.apus.edu/).

It contains a simple TCP socket server that is capable of receiving a list of
bitmap drawing instructions and then producing an output image from them that
is then returned to the client.

To run the example program:

```bash
cd RenderSiren
java -jar out/RunServer.jar

# Open another terminal

cd RenderSiren
java -jar out/RunClient.jar
```
