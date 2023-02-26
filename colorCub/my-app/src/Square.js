import "./App.css"

const Square = ({color, fontColor}) => {
  const colorStr = color ? color : "Please Input the color"
  return (
    <div 
      className="Square"
      style={
        {backgroundColor: color,
        color: fontColor}
      }
    >
      <p>{colorStr}</p>
    </div>
  )
}

export default Square;