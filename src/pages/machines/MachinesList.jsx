import { useState, useEffect } from 'react';
import { MACHINE_DATA } from '../../api/mock_data';
import MachineTabelBig from '../../components/machines/MachineTabelBig';

const MachinesList = () => {
  const [machines, setMachines] = useState(MACHINE_DATA);
  const [text, setText] = useState('');

  useEffect(() => {
    const filteredMachines = MACHINE_DATA.filter((m) => {
      return m.id.toString().includes(text.toLowerCase());
    });
    setMachines(filteredMachines);
  }, [text]);

  return (
    <div className='machine-tabel-big-container'>
      <div className='input-group'>
        <input
          type='search'
          id='search'
          className='form-control rounded'
          placeholder='Search'
          value={text}
          onChange={(e) => setText(e.target.value)}
        />
      </div>
      <MachineTabelBig machines={machines}/>
    </div>
  );
};

export default MachinesList;