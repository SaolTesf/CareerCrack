import logo from './Components/Assets/CareerCrack-logo.png';
import './App.css';
import { LoginSignup } from './Components/LoginSignUp/LoginSignup';

function App() {
  return (
    <div>
      <div className='logo-container'>
        <img src={logo} alt='CareerCrack logo' className='App-logo' />
      </div>
      <LoginSignup></LoginSignup>
    </div>
  );
}

export default App;
