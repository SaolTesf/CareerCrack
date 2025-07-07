import logo from './Components/Assets/CareerCrack-logo.png';
import './App.css';
import { LoginSignup } from './Components/LoginSignUp/LoginSignup';

function App() {
  return (
    <div>
      <img src={logo} alt='CareerCrack logo' className='App-logo' />
      <LoginSignup></LoginSignup>
    </div>
  );
}

export default App;
