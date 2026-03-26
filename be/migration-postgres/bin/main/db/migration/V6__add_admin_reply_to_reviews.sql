-- Add admin reply fields to product_reviews table
ALTER TABLE product_reviews 
DROP COLUMN IF EXISTS helpful_count,
ADD COLUMN like_count INT NOT NULL DEFAULT 0,
ADD COLUMN dislike_count INT NOT NULL DEFAULT 0,
ADD COLUMN admin_reply TEXT,
ADD COLUMN admin_reply_at TIMESTAMP,
ADD COLUMN admin_id BIGINT,
ADD CONSTRAINT fk_product_reviews_admin FOREIGN KEY (admin_id) REFERENCES users(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_product_reviews_admin_id ON product_reviews(admin_id);

-- Create table to track user likes/dislikes
CREATE TABLE IF NOT EXISTS review_reactions (
    id BIGSERIAL PRIMARY KEY,
    review_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reaction_type VARCHAR(10) NOT NULL CHECK (reaction_type IN ('LIKE', 'DISLIKE')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_reactions_review FOREIGN KEY (review_id) REFERENCES product_reviews(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_reactions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_review_user_reaction UNIQUE (review_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_review_reactions_review_id ON review_reactions(review_id);
CREATE INDEX IF NOT EXISTS idx_review_reactions_user_id ON review_reactions(user_id);