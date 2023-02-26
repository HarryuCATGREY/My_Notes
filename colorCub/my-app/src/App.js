
import './App.css';
import {useState} from 'react';
import Square from './Square';
import Input from "./Input"


function App() {
  const [color, setColor] = useState("")
  const [fontColor, setFontColor] = useState("")
  
  
  return (
    // <div className="App">
    //   <div 
    //     className="colorBox"
    //     style={{backgroundColor: color}}
    //   ></div>
    //   <input
    //     type="text"
    //     className="inputMessage"
    //   ></input>
    // </div>
    <div className="App">
      <Square className="Square"
        color = {color}
        fontColor = {fontColor}
      />
      <Input
        color = {color} 
        setColor = {setColor}
      />
      <Input
        color = {fontColor} 
        setColor = {setFontColor}
      />

    </div>
  );
}

export default App;
