import "./App.css"

const Input = ({setColor, color}) => {
  return (
    <form 
      onSubmit={(e) => e.preventDefault}
    >
      <input
        focus
        type="text"
        placeholder="change the color here"
        value={color}
        onChange={(e)=> setColor(e.target.value)}
      ></input>
    </form>
  )
}

export default Input;