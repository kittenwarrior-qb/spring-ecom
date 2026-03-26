-- Migration: Change from ManyToMany to OneToMany relationship
-- Drop the junction table and add category_id to products table

-- Step 1: Add category_id column to products table
ALTER TABLE products ADD COLUMN category_id BIGINT;

-- Step 2: Migrate data from product_categories to products.category_id
-- This assumes each product will have only one primary category
-- You may need to adjust this logic based on your business rules
UPDATE products p 
SET category_id = (
    SELECT pc.category_id 
    FROM product_categories pc 
    WHERE pc.product_id = p.id 
    LIMIT 1
);

-- Step 3: Add foreign key constraint
ALTER TABLE products 
ADD CONSTRAINT fk_products_category 
FOREIGN KEY (category_id) REFERENCES categories(id);

-- Step 4: Drop the junction table
DROP TABLE product_categories;

-- Optional: Add index for better query performance
CREATE INDEX idx_products_category_id ON products(category_id);