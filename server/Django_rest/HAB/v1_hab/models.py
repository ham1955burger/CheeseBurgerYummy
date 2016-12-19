from django.db import models
from django.utils import timezone
from sorl.thumbnail import ImageField
from sorl.thumbnail import delete

CATEGORY_CHOICES = (
    ('salary', '월급'),
    ('foodExpenses', '식비'),
    ('default', '기타'),
)

STATE_CHOICES = (
    ('receive', '입금'),
    ('pay', '출금'),
)

class HouseholdAccountBook(models.Model):
    date = models.DateField(default=timezone.now)
    price = models.PositiveIntegerField()
    state = models.CharField(choices=STATE_CHOICES, max_length=10)
    category = models.CharField(choices=CATEGORY_CHOICES, default='default', max_length=10)
    priority = models.SmallIntegerField(default=0)
    memo = models.CharField(blank=True, max_length=100)

    class Meta:
        ordering = ('-priority'),


class Photo(models.Model):
    image_file = ImageField(upload_to='%Y/%m/%d')
    # image_thumb = ImageField(upload_to='thumbnails/%Y/%m/%d')
    description = models.TextField(max_length=500, blank=True)
    created_at = models.DateTimeField(auto_now_add=True, auto_now=False)

    class Meta:
        ordering = ('-created_at'),

    def delete(self, *args, **kwargs):
        # 순서주의! cached db에서 지워준 후, 본 db에서 삭제
        # delete(self.image_file)
        # self.image_file.delete()
        # self.image_thumb.delete()
        super(Photo, self).delete(*args, **kwargs)
