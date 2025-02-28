import { useState } from 'react';
import { MACHINE_DATA } from '../../api/mock_data';
import MachineTabelBig from '../../components/machines/MachineTabelBig';

const MachinesList = () => {
  const [machines, setMachines] = useState(MACHINE_DATA);
  const [text, setText] = useState('');
  const [search, setSearch] = useState('');

  return (
    <div className='machine-tabel-big-container'>
      <MachineTabelBig machines={machines}/>
    </div>
  );
};

export default MachinesList;