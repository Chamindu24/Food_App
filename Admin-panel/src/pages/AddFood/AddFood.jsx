import React, {  useState } from 'react'
import { assets } from '../../assets/assets';
import axios from 'axios';

const AddFood = () => {
  const [image, setImage] = useState(null);
  const [data, setData] = useState({
    name: "",
    description: "",
    category: "Biriyani",
    price: ""
  });

  const onChangeHandler = (e) => {
    const { name, value } = e.target;
    setData(data => ({ ...data, [name]: value }));
  }


  const onSubmitHandler = async (e) => {
    e.preventDefault(); // ✅ fixed typo

    if (!image) {
      alert("Please select an image");
      return;
    }

    const formData = new FormData();
    formData.append('food', JSON.stringify(data));
    formData.append('file', image);

    try {
      const response = await axios.post('http://localhost:8081/api/foods/', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });

      if (response.status === 200) {
        alert("Food added successfully");
        setData({
          name: "",
          description: "",
          category: "Biriyani",
          price: ""
        });
        setImage(null);
      }
    } catch (err) {
      console.log("Error", err);
      alert(err?.response?.data?.message || "Error adding foods");
    }
  };

  return (
    <div className="mt-2 mx-2">
      <div className="row">
        <div className="card col-md-4">
          <div className="card-body contact-form">
            <h2 className="mb-4">Add Food</h2>
            <form onSubmit={onSubmitHandler}>
              <div className="mb-3">
                <label htmlFor="image" className="form-label">
                  <img src={image ? URL.createObjectURL(image) : assets.upload} alt="" width={98} />
                </label>
                <input type="file" className="form-control cursor-pointer" id="image" hidden onChange={(e) => setImage(e.target.files[0])} />
              </div>

              <div className="mb-3">
                <label htmlFor="name" className="form-label">Name</label>
                <input type="text" className="form-control" id="name" required name='name' onChange={onChangeHandler} value={data.name} />
              </div>

              <div className="mb-3">
                <label htmlFor="description" className="form-label">Description</label>
                <textarea className="form-control" id="description" rows="5" required name='description' onChange={onChangeHandler} value={data.description}></textarea>
              </div>

              <div className="mb-3">
                <label htmlFor="category" className="form-label">Category</label>
                <select name="category" id="category" className="form-control" onChange={onChangeHandler} value={data.category}>
                  <option value="Biriyani">Biriyani</option>
                  <option value="Pizza">Pizza</option>
                  <option value="Burger">Burger</option>
                  <option value="Pasta">Pasta</option>
                  <option value="Salad">Salad</option>
                  <option value="Sushi">Sushi</option>
                  <option value="Tacos">Tacos</option>
                  <option value="Steak">Steak</option>
                  <option value="Sandwich">Sandwich</option>
                  <option value="Soup">Soup</option>
                </select>
              </div>

              <div className="mb-3">
                <label htmlFor="price" className="form-label">Price</label>
                <input type="number" className="form-control" id="price" required name='price' onChange={onChangeHandler} value={data.price} />
              </div>

              <button type="submit" className="btn btn-primary">Save</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddFood;
